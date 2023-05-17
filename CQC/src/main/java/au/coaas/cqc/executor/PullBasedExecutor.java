package au.coaas.cqc.executor;

import au.coaas.base.proto.ListOfString;

import au.coaas.cpree.proto.CPREEServiceGrpc;
import au.coaas.cpree.proto.CacheSelectionRequest;
import au.coaas.cpree.proto.ContextRefreshRequest;

import au.coaas.cqc.proto.Empty;
import au.coaas.cqc.utils.ConQEngHelper;
import au.coaas.cqc.utils.enums.CacheLevels;

import au.coaas.cre.proto.*;

import au.coaas.cqc.utils.Utilities;
import au.coaas.cqc.utils.enums.MeasuredProperty;
import au.coaas.cqc.utils.exceptions.ExtendedToken;
import au.coaas.cqc.utils.exceptions.WrongOperatorException;

import au.coaas.grpc.client.CPREEChannel;
import au.coaas.grpc.client.CREChannel;
import au.coaas.grpc.client.SQEMChannel;

import au.coaas.cqc.proto.*;
import au.coaas.cqp.proto.*;
import au.coaas.sqem.proto.*;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.common.util.concurrent.ListenableFuture;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.function.Predicate;
import java.util.concurrent.Executors;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

public class PullBasedExecutor {

    // This is the cache switch
    private static boolean cacheEnabled = true;
    private static boolean registerState = false;

    private static Logger log = Logger.getLogger(PullBasedExecutor.class.getName());
    private static JsonParser parser = new JsonParser();

    private static List<FunctionCall> getFunctionCalls(Queue<CdqlConditionToken> tokens) {
        List<FunctionCall> result = new ArrayList<>();
        for (CdqlConditionToken token : tokens) {
            if (token.getType() == CdqlConditionTokenType.Function) {
                FunctionCall fCall = token.getFunctionCall();
                result.add(fCall);
                result.addAll(getSubFunctionCalls(fCall));
            }
        }
        return result;
    }

    private static Gson gson = new Gson();

    private static List<FunctionCall> getSubFunctionCalls(FunctionCall fCall) {
        List<FunctionCall> result = new ArrayList<>();
        for (Operand argument : fCall.getArgumentsList()) {
            if (argument.getType() == OperandType.FUNCTION_CALL) {
                FunctionCall subFCall = argument.getFunctioncall();
                result.add(subFCall);
                result.addAll(getSubFunctionCalls(subFCall));
            }
        }
        return result;
    }

    public static Empty updateRegistryState(boolean state){
        registerState = state;
        return null;
    }

    // This method executes the query plan for pull based queries
    public static CdqlResponse executePullBaseQuery(CDQLQuery query, String authToken, int page, int limit,
                                                    String queryId, String criticality, double complexity) throws Exception{

        // Initialize values
        // How are the values assigned to 'ce'? I only see values assigned in the catch block.
        Map<String, JSONObject> ce = new HashMap<>();
        JSONObject consumerQoS = null;

        // Iterates over execution plan
        // The first loop goes over dependent sets of entities
        Collection<ListOfString> aList = query.getExecutionPlanMap().values();

        // Fetching Consumer SLA
        JSONObject sla = null;
        SQEMServiceGrpc.SQEMServiceFutureStub sqemStub
                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
        ListenableFuture<SQEMResponse> slaMessage = sqemStub.getConsumerSLA(AuthToken.newBuilder().setToken(authToken).build());

        // Replace with bList for Testing
        for (ListOfString entityList : aList) {
            // Second loop iterates over the entities in the dependent set
            for (String entityID : entityList.getValueList()) {
                ContextEntity entity = query.getDefine().getDefinedEntitiesList().stream().filter(v -> v.getEntityID().equals(entityID)).findFirst().get();
                // Here the entity ID is the aliase that is defined in the query. e.g., e1 in "define entity e1 is from swm:type1"

                Queue<CdqlConditionToken> rpnCondition = new LinkedList<>(entity.getCondition().getRPNConditionList());

                int i = 0;
                Map<String, String> terms = new HashMap<>();
                boolean errorDetected = false;

                // Third loop iterates over the RPN Conditions of an entity
                // This loop cleans the RPN Conditions to form that is in line with the rest of the logic.
                // For example, {"unit":"km","value":2} is converted to 2000
                while (rpnCondition.peek() != null) {
                    try {
                        CdqlConditionToken token = rpnCondition.poll();
                        if(token.getType().equals(CdqlConditionTokenType.Function)){
                            // Skipping the conditional value
                            rpnCondition.poll();
                            // Skipping the operand
                            rpnCondition.poll().getStringValue();
                            continue;
                        }
                        String attributeName = token.getStringValue();
                        if (attributeName.equals("and") || attributeName.equals("or"))
                            continue;

                        // Take only the identifier of the context attribute
                        // e.g., e1.attr1 --> key = attr1
                        String key = attributeName.replace(entity.getEntityID() + ".", "");
                        // Take the value on the right side of the operator
                        CdqlConditionToken valueToken = rpnCondition.poll();
                        String value = valueToken.getStringValue();
                        // e.g., "\"value"\" --> "value"
                        if (value.contains("\"")) {
                            value = value.replaceAll("\"", "");
                            String pathEncode = URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8.toString());
                            // e.g., term <att1,"value">
                            terms.put(key, pathEncode);
                            // serviceURI = serviceURI.replace("{" + key + "}", pathEncode);
                        }
                        else if (value.contains("{")) { }
                        else if (valueToken.getType() != CdqlConditionTokenType.Constant && (ce.containsKey(value) || value.contains("."))) {
                            // This step sees if the value of the condition is an attribute of another entity
                            String valueEntityID = value.split("\\.")[0];
                            String valueEntityBody = value.replace(valueEntityID + ".", "");
                            Object get = null;
                            if (valueEntityID.equals(valueEntityBody)) {
                                get = ce.get(valueEntityID);
                            } else {
                                try {
                                    // What happens here is, the query contains a condition that compares that values of an existing entity.
                                    // So, this try to get the matched attribute (i.e., valueEntityBody) value from the 'ce'.
                                    get = Utilities.getValueOfJsonObject(ce.get(valueEntityID), valueEntityBody);
                                } catch (Exception ex) {
                                    get = Utilities.getValueOfJsonObject(ce.get(valueEntityID).getJSONArray("results").getJSONObject(0), valueEntityBody);
                                }
                            }
                            if (get instanceof JSONObject) {
                                JSONObject tmp = (JSONObject) get;
                                Set<String> keySet = tmp.keySet();
                                if(keySet.contains("value")){
                                    Object subItemValue = tmp.get("value");
                                    if (!(subItemValue instanceof JSONObject)) {
                                        String pathEncode = URLEncoder.encode(subItemValue.toString(), java.nio.charset.StandardCharsets.UTF_8.toString());
                                        terms.put(key, pathEncode);
                                    }
                                }
                                else {
                                    for (String jsonKey : tmp.keySet()) {
                                        Object subItemValue = tmp.get(jsonKey);
                                        if (!(subItemValue instanceof JSONObject)) {
                                            String pathEncode = URLEncoder.encode(subItemValue.toString(), java.nio.charset.StandardCharsets.UTF_8.toString());
                                            terms.put(key + "." + jsonKey, pathEncode);
                                        }
                                    }
                                }
                            } else {
                                String temp = String.valueOf(get).replaceAll("\"", "");
                                String pathEncode = URLEncoder.encode(temp, java.nio.charset.StandardCharsets.UTF_8.toString());
                                terms.put(key, pathEncode);
                            }

                        } else {
                            String pathEncode = URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8.toString());
                            terms.put(key, pathEncode);
                        }
                        // This is the third part of the expression - Operator
                        // e.g., "=". This is only to skip to the next operand. Operator is not used here.
                        rpnCondition.poll();

                    } catch (Exception ex) {
                        Logger.getLogger(PullBasedExecutor.class
                                .getName()).log(Level.SEVERE, null, ex);
                        JSONObject error = new JSONObject();
                        error.put("error", ex.getMessage());
                        ce.put(entity.getEntityID(), error);
                        errorDetected = true;
                        break;
                    }
                }

                if (errorDetected) {
                    break;
                }

                String contextService = ContextServiceDiscovery.discover(entity.getType(), terms.keySet());

                if (contextService != null) {
                    ContextRequest contextRequest = generateContextRequest(entity, query, ce, 0, 0);
                    List<CdqlConditionToken> rpnConditionList = contextRequest.getCondition().getRPNConditionList();

                    String key = null;
                    String value = null;

                    HashMap<String,String> params = new HashMap();

                    for (CdqlConditionToken cdqlConditionToken : rpnConditionList) {
                        switch (cdqlConditionToken.getType()) {
                            case Attribute:
                                key = cdqlConditionToken.getContextAttribute().getAttributeName();
                                if (value != null) {
                                    params.put(key,value);
                                    key = null;
                                    value = null;
                                }
                                break;
                            case Constant:
                                value = cdqlConditionToken.getStringValue();
                                if(value.startsWith("{")){
                                    JSONObject valObj = new JSONObject(value);
                                    // TODO:
                                    // Should modify the following to converted to the defined unit of the Consumer SLA
                                    if(valObj.has("value"))
                                        value = valObj.get("value").toString();
                                }
                                if (key != null) {
                                    params.put(key,value);
                                    key = null;
                                }
                                value = null;
                                break;
                        }
                    }

                    if(sla == null) {
                        SQEMResponse slaResult = slaMessage.get();
                        if(slaResult.getStatus().equals("200")){
                            sla = new JSONObject(slaResult.getBody());
                            JSONObject finalSla = sla;
                            // Profiling the context consumers
                            Executors.newCachedThreadPool().execute(() -> profileConsumer(finalSla,queryId));
                        }
                    }

                    AbstractMap.SimpleEntry cacheResult = retrieveContext(entity, contextService, params, limit,
                                                                    criticality, sla, complexity, terms.keySet());

                    if(cacheResult != null){
                        JSONObject resultJson = new JSONObject((String) cacheResult.getKey());
                        ce.put(entity.getEntityID(),resultJson);
                        if(query.getSelect().getSelectAttrsMap().containsKey(entity.getEntityID()))
                            executeQueryBlock(rpnConditionList, ce, entityID);
                        if(consumerQoS == null)  consumerQoS = (JSONObject) cacheResult.getValue();
                    }
                    else {
                        // When the data can not be retrieved from any context providers, checking the stored data as the last resort.
                        AbstractMap.SimpleEntry rex = executeSQEMQuery(entity, query, ce, page, limit, sla);
                        ce.put(entity.getEntityID(), (JSONObject) rex.getKey());
                        if(consumerQoS == null)  consumerQoS = (JSONObject) rex.getValue();
                    }

                }
                else {
                    // Querying from registered entities. This does not guarantee "completeness" of context.
                    // Registered entities updated. But not the most fresh. So, best when the entities are static, e.g., buildings.
                    // In fact, querying from MongoDB is actually to query the "Context Service Descriptions".
                    // This can also be if the context service is a data stream.

                    if(sla == null) {
                        SQEMResponse slaResult = slaMessage.get();
                        if(slaResult.getStatus().equals("200")){
                            sla = new JSONObject(slaResult.getBody());
                            JSONObject finalSla = sla;
                            // Profiling the context consumers
                            Executors.newCachedThreadPool().execute(() -> profileConsumer(finalSla,queryId));
                        }
                    }

                    AbstractMap.SimpleEntry rex = executeSQEMQuery(entity, query, ce, page, limit, sla);
                    ce.put(entity.getEntityID(), (JSONObject) rex.getKey());
                    if(consumerQoS == null)  consumerQoS = (JSONObject) rex.getValue();
                    continue;
                }

                //ToDo function call and discovery
            }
            // End of iterating through the entity list and fetching for them.
        }
        // End of iterating through the execution plan

        JSONObject result = new JSONObject();

        // Filter out the neccesary attributes from the retrieved entity data
        query.getSelect().getSelectAttrsMap().entrySet().parallelStream().forEach((entry) -> {
            String entity = entry.getKey();
            result.put(entity, ce.get(entity));
        });

        query.getSelect().getSelectFunctionsList().parallelStream().forEach((fCall) -> {
            if (fCall.getFunctionName().equals("avg") || fCall.getFunctionName().equals("cluster")) {
                result.put(fCall.getFunctionName(), executeFunctionCall(fCall, ce, query));
            } else {
                // All other context functions that are not either "avg" or "cluster".
                Object execute = executeFunctionCall(fCall, ce, query);
                result.put(fCall.getFunctionName(), execute);
//              if (execute.trim().startsWith("[")) {
//                  result.put(fCall.getFunctionName(), new JSONArray(execute.replaceAll("\"", "")));
//              } else {
//                  if (fCall.getSubItemsList().isEmpty()) {
//                      result.put(fCall.getFunctionName(), execute);
//                  } else {
//                      result.put(fCall.getSubItemsList().get(0), execute);
//                  }
//              }
            }
        });

        //String queryOuptput = OutputHandler.handle(result, query.getConfig().getOutputConfig());
        // If this response need to be cached, this is where the caching action should happen.
        CdqlResponse cdqlResponse = CdqlResponse.newBuilder().setStatus("200")
                .setQueryId(queryId)
                .setBody(result.toString())
                .setAdmin(CdqlAdmin.newBuilder()
                        .setRtmax(consumerQoS.getJSONObject("rtmax").getLong("value"))
                        .setPrice(consumerQoS.getDouble("price"))
                        .setRtpenalty(consumerQoS.getJSONObject("rtmax").getJSONObject("penalty")
                                .getDouble("value"))
                        .build())
                .build();

        return cdqlResponse;
    }

    private static void profileConsumer(JSONObject sla, String queryId){
        SQEMServiceGrpc.SQEMServiceFutureStub sqemStub
                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
        sqemStub.logConsumerProfile(SummarySLA.newBuilder()
                .setCsid(sla.getJSONObject("_id").getString("$oid"))
                .setFthresh(sla.getJSONObject("sla").getJSONObject("qos").getDouble("fthresh"))
                .setEarning(sla.getJSONObject("sla").getJSONObject("price").getDouble("value"))
                .setRtmax(sla.getJSONObject("sla").getJSONObject("qos")
                        .getJSONObject("rtmax").getLong("value"))
                .setPenalty(sla.getJSONObject("sla").getJSONObject("qos").getJSONObject("rtmax")
                        .getJSONObject("penalty").getDouble("value"))
                .setQueryId(queryId)
                .setQueryClass(1).build()); // TODO: Assign the correct query class
    }

    private static void executeQueryBlock(List<CdqlConditionToken> rpnConditionList,
                                          Map<String, JSONObject> ce, String entityId) throws Exception {
        Queue<CdqlConditionToken> RPNcondition = new LinkedList<>(rpnConditionList);
        Stack<ExtendedToken> stack = new Stack<>();
        Stack<Predicate<JSONObject>> filter = new Stack<>();

        if (!RPNcondition.isEmpty()) {
            ExtendedToken extendedToken = new ExtendedToken(RPNcondition.poll());
            CdqlConditionToken token = extendedToken.getCdqlConditionToken();
            while (token != null) {
                switch (token.getType()) {
                    case Attribute:
                        stack.push(new ExtendedToken(token));
                        break;
                    case Constant:
                        stack.push(extendedToken);
                        break;
                    case Function:
                        String value = RPNcondition.poll().getStringValue();
                        String operand = RPNcondition.poll().getStringValue();
                        stack.push(new ExtendedToken(CdqlConditionToken.newBuilder()
                                .setType(CdqlConditionTokenType.Constant)
                                .setConstantTokenType(CdqlConstantConditionTokenType.Array).build(),
                                executeFunction(token.getFunctionCall(), ce, operand, value)));
                        break;
                    case Operator:
                        processOperations(token.getStringValue(), stack, RPNcondition, filter, ce, entityId);
                        break;
                }
                if(!RPNcondition.isEmpty()){
                    extendedToken = new ExtendedToken(RPNcondition.poll());
                    token = extendedToken.getCdqlConditionToken();
                } else
                    token = null;
            }

            JSONObject finalResult = new JSONObject();
            finalResult.put("results", stack.pop().getData());
            ce.put(entityId, finalResult);
        }
    }

    private static void processOperations(String operator, Stack<ExtendedToken> stack,
                                          Queue<CdqlConditionToken> RPNcondition, Stack<Predicate<JSONObject>> filter,
                                          Map<String,JSONObject> ce, String entityId) throws WrongOperatorException {

        switch (operator) {
            case "=": {
                ExtendedToken operand_2 = stack.pop();
                ExtendedToken operand_1 = stack.pop();

                if (operand_1.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant &&
                        operand_2.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant) {
                    stack.push(processConstantValues(operand_1, operand_2, operator));
                    break;
                }

                if (operand_1.getCdqlConditionToken().getType() != CdqlConditionTokenType.Attribute)
                    throw new WrongOperatorException("The first operand in the expression should be an attribute");

                if (operand_2.getCdqlConditionToken().getType() != CdqlConditionTokenType.Constant)
                    throw new WrongOperatorException("The second operand in the expression should be a constant value");

                if(operand_1.getCdqlConditionToken().getType() == CdqlConditionTokenType.Attribute &&
                        operand_2.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant){
                    switch(operand_2.getCdqlConditionToken().getConstantTokenType()){
                        case Numeric:
                            filter.add( x -> x.getDouble(operand_1.getCdqlConditionToken()
                                    .getContextAttribute().getAttributeName()) ==
                                    Double.valueOf(operand_2.getCdqlConditionToken().getStringValue()));
                            break;
                        case String:
                            String opValue = operand_2.getCdqlConditionToken().getStringValue();
                            if(opValue.startsWith("{")){
                                opValue = (new JSONObject(opValue)).getString("value");
                                String finalOpValue = opValue;
                                if(getConstantType(opValue) == CdqlConstantConditionTokenType.Numeric) {
                                    filter.add(x -> x.getDouble(operand_1.getCdqlConditionToken()
                                                    .getContextAttribute().getAttributeName()) == Double.valueOf(finalOpValue));
                                }
                            }
                            String finalOpValue1 = opValue;
                            filter.add(x -> x.getString(operand_1.getCdqlConditionToken()
                                    .getContextAttribute().getAttributeName())
                                    .equals(finalOpValue1));
                            break;
                        case Boolean:
                            String attName = operand_1.getCdqlConditionToken().getContextAttribute().getAttributeName();
                            Boolean value = Boolean.valueOf(operand_2.getCdqlConditionToken().getStringValue());
                            filter.add( x -> x.getBoolean(attName) == value);
                            break;
                    }
                }

                break;
            }

            case "and": {
                if(stack.size() < 2 && filter.size() > 0){
                    List<Predicate<JSONObject>> filters = new ArrayList<>();
                    while (!RPNcondition.isEmpty() && !filter.isEmpty() &&
                            "and".equals(RPNcondition.peek().getStringValue())) {
                        RPNcondition.poll();
                        filters.add(filter.pop());
                    }
                    filters.add(filter.pop());
                    List<JSONObject> result = new ArrayList<>();
                    if(ce.get(entityId).has("results"))
                        ce.get(entityId).getJSONArray("results").forEach(x -> result.add((JSONObject) x));
                    else if(ce.get(entityId).length() != 0)
                        result.add(ce.get(entityId));

                    List<JSONObject> dataList = result.stream().filter(filters.stream().reduce(x -> true, Predicate::and))
                            .collect(Collectors.toList());
                    if(stack.size()>=1)
                        stack.push(intersection((JSONArray) stack.pop().getData(), new JSONArray(dataList)));
                    else{
                        ExtendedToken dataToken = new ExtendedToken(CdqlConditionToken.newBuilder()
                                .setType(CdqlConditionTokenType.Constant)
                                .setConstantTokenType(CdqlConstantConditionTokenType.Array)
                                .build());
                        dataToken.setData(dataList);
                        stack.push(dataToken);
                    }
                    break;
                }

                ExtendedToken operand_2 = stack.pop();
                ExtendedToken operand_1 = stack.pop();

                if(operand_1.getCdqlConditionToken().getType() == operand_2.getCdqlConditionToken().getType()){
                    if(operand_1.getCdqlConditionToken().getConstantTokenType() == operand_2.getCdqlConditionToken().getConstantTokenType()){
                        switch (operand_1.getCdqlConditionToken().getConstantTokenType()){
                            case Numeric:
                            case Boolean:
                            case String:
                                stack.push(processConstantValues(operand_1, operand_2, operator));
                                break;
                            case Array:
                                stack.push(intersection((JSONArray) operand_1.getData(),
                                        (JSONArray) operand_2.getData()));
                                break;
                            default:
                                throw new WrongOperatorException("Cannot apply AND operator for the given operands.");
                        }
                    }

                    if(operand_1.getCdqlConditionToken().getConstantTokenType() == CdqlConstantConditionTokenType.Boolean ||
                            operand_2.getCdqlConditionToken().getConstantTokenType() == CdqlConstantConditionTokenType.Boolean){
                        if(operand_2.getCdqlConditionToken().getConstantTokenType() == CdqlConstantConditionTokenType.Boolean &&
                                Boolean.valueOf(operand_2.getCdqlConditionToken().getStringValue())){
                            stack.push(operand_1);
                            break;
                        }
                        if(operand_1.getCdqlConditionToken().getConstantTokenType() == CdqlConstantConditionTokenType.Boolean &&
                                Boolean.valueOf(operand_1.getCdqlConditionToken().getStringValue())){
                            stack.push(operand_2);
                            break;
                        }

                        operand_1.setData(new JSONArray());
                        stack.push(operand_1);
                        break;
                    }
                }

                if(operand_1.getCdqlConditionToken().getType() == CdqlConditionTokenType.Attribute &&
                        operand_2.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant){
                    while (!RPNcondition.isEmpty() && "and".equals(RPNcondition.peek().getStringValue())) {
                        RPNcondition.poll();
                        ExtendedToken operand3 = stack.pop();
                        if (operand3.getCdqlConditionToken().getType() != CdqlConditionTokenType.Expression) {
                            throw new WrongOperatorException("Wrong Operand. Expression expected.");
                        }
                    }
                }

                break;
            }
            case "or": {
                if(stack.size() < 2 && filter.size() > 0){
                    List<Predicate<JSONObject>> filters = new ArrayList<>();
                    while (!RPNcondition.isEmpty() && !filter.isEmpty() &&
                            "and".equals(RPNcondition.peek().getStringValue())) {
                        RPNcondition.poll();
                        filters.add(filter.pop());
                    }
                    filters.add(filter.pop());
                    List<JSONObject> result = new ArrayList<>();
                    if(ce.get(entityId).has("results"))
                        ce.get(entityId).getJSONArray("results").forEach(x -> result.add((JSONObject) x));
                    else if(ce.get(entityId).length() != 0)
                        result.add(ce.get(entityId));

                    List<JSONObject> dataList = result.stream().filter(filters.stream().reduce(x -> true, Predicate::and))
                            .collect(Collectors.toList());
                    stack.push(union((JSONArray) stack.pop().getData(), new JSONArray(dataList)));
                    break;
                }

                ExtendedToken operand_2 = stack.pop();
                ExtendedToken operand_1 = stack.pop();

                if(operand_1.getCdqlConditionToken().getType() == operand_2.getCdqlConditionToken().getType()){
                    if(operand_1.getCdqlConditionToken().getConstantTokenType() == operand_2.getCdqlConditionToken().getConstantTokenType()){
                        switch (operand_1.getCdqlConditionToken().getConstantTokenType()){
                            case Numeric:
                            case Boolean:
                            case String:
                                stack.push(processConstantValues(operand_1, operand_2, operator));
                                break;
                            case Array:
                                stack.push(union((JSONArray) operand_1.getData(),
                                        (JSONArray) operand_2.getData()));
                                break;
                            default:
                                throw new WrongOperatorException("Cannot apply AND operator for the given operands.");
                        }
                    }

                    if(operand_1.getCdqlConditionToken().getConstantTokenType() == CdqlConstantConditionTokenType.Boolean ||
                            operand_2.getCdqlConditionToken().getConstantTokenType() == CdqlConstantConditionTokenType.Boolean){
                        stack.push(operand_1);
                        break;
                    }

                }

                break;
            }
            default: {
                ExtendedToken operand_2 = stack.pop();
                ExtendedToken operand_1 = stack.pop();

                if (operand_1.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant
                        && operand_2.getCdqlConditionToken().getType() == CdqlConditionTokenType.Constant) {
                    stack.push(processConstantValues(operand_1, operand_2, operator));
                    break;
                }

                if (operand_1.getCdqlConditionToken().getType() != CdqlConditionTokenType.Attribute)
                    throw new WrongOperatorException("the first operand in the expression should be an attribute");

                if (operand_2.getCdqlConditionToken().getType() != CdqlConditionTokenType.Constant)
                    throw new WrongOperatorException("the second operand in the expression should be a constant value");

                filter.push(getPredicate(operand_2.getCdqlConditionToken().getConstantTokenType(),
                        operator, operand_1.getCdqlConditionToken().getContextAttribute().getAttributeName(),
                        operand_2.getCdqlConditionToken().getStringValue()));
            }
        }
    }

    private static Predicate<JSONObject> getPredicate(CdqlConstantConditionTokenType type,
                                                      String operator,
                                                      String attribute,
                                                      String value) throws WrongOperatorException {
        switch(type){
            case Numeric:
                if(operator.equals(">"))
                    return x -> x.getDouble(attribute) > Double.valueOf(value);
                if(operator.equals(">="))
                    return x -> x.getDouble(attribute) >= Double.valueOf(value);
                if(operator.equals("<"))
                    return x -> x.getDouble(attribute) < Double.valueOf(value);
                if(operator.equals("<="))
                    return x -> x.getDouble(attribute) <= Double.valueOf(value);
                if(operator.equals("!="))
                    return x -> x.getDouble(attribute) != Double.valueOf(value);
                throw new WrongOperatorException("Can not apply this operator on Numerics.");
            case String:
                if(value.startsWith("{")){
                    String finalValue = (new JSONObject(value)).getString("value");
                    if(getConstantType(finalValue) == CdqlConstantConditionTokenType.Numeric) {
                        if(operator.equals("="))
                            return x -> x.getDouble(attribute) == Double.valueOf(finalValue);
                    }
                    else if(operator.equals("="))
                        return x -> x.getString(attribute).equals(finalValue);
                }
                else {
                    if(operator.equals("=")) {
                        return x -> x.getString(attribute).equals(value);
                    }
                }
                throw new WrongOperatorException("Can not apply this operator on Strings.");
            case Json:
                String finalValue = (new JSONObject(value)).get("value").toString();
                if(getConstantType(finalValue) == CdqlConstantConditionTokenType.Numeric) {
                    if(operator.equals("="))
                        return x -> x.getDouble(attribute) == Double.valueOf(finalValue);
                    if(operator.equals(">"))
                        return x -> x.getDouble(attribute) > Double.valueOf(finalValue);
                    if(operator.equals(">="))
                        return x -> x.getDouble(attribute) >= Double.valueOf(finalValue);
                    if(operator.equals("<"))
                        return x -> x.getDouble(attribute) < Double.valueOf(finalValue);
                    if(operator.equals("<="))
                        return x -> x.getDouble(attribute) <= Double.valueOf(finalValue);
                    if(operator.equals("!="))
                        return x -> x.getDouble(attribute) != Double.valueOf(finalValue);
                }
                else if(operator.equals("="))
                    return x -> x.getString(attribute).equals(finalValue);

                throw new WrongOperatorException("Can not apply this operator on Strings.");
            case Boolean:
                if(operator.equals("="))
                    return x -> x.getBoolean(attribute) == Boolean.valueOf(value);
                if(operator.equals("!="))
                    return x -> x.getBoolean(attribute) != Boolean.valueOf(value);
                throw new WrongOperatorException("Can not apply this operator on Booleans.");
            default:
                throw new WrongOperatorException("Can not apply this operator.");
        }
    }

    private static ExtendedToken intersection(JSONArray results1, JSONArray results2) throws JSONException {
        ExtendedToken dataToken = new ExtendedToken(CdqlConditionToken.newBuilder()
                .setType(CdqlConditionTokenType.Constant)
                .setConstantTokenType(CdqlConstantConditionTokenType.Array)
                .build());

        Set<JSONObject> firstAsSet = convertJSONArrayToSet(results1);
        Set<JSONObject> secondIdsAsSet = convertJSONArrayToSet(results2);
        Set<JSONObject> intersection = firstAsSet.stream()
                .distinct()
                .filter(secondIdsAsSet::contains)
                .collect(Collectors.toSet());

        dataToken.setData(intersection.toArray());
        return dataToken;
    }

    private static ExtendedToken union(JSONArray results1, JSONArray results2){
        ExtendedToken dataToken = new ExtendedToken(CdqlConditionToken.newBuilder()
                .setType(CdqlConditionTokenType.Constant)
                .setConstantTokenType(CdqlConstantConditionTokenType.Array)
                .build());

        Set<JSONObject> firstAsSet = convertJSONArrayToSet(results1);
        Set<JSONObject> secondIdsAsSet = convertJSONArrayToSet(results2);
        Set<JSONObject>  union = Stream.concat(firstAsSet.stream(), secondIdsAsSet.stream())
                                    .collect(Collectors.toSet());

        dataToken.setData(union.toArray());
        return dataToken;
    }

    private static Set<JSONObject> convertJSONArrayToSet(JSONArray input) throws JSONException {
        Set<JSONObject> retVal = new HashSet<>();
        for (Object item: input) {
            retVal.add((JSONObject) item);
        }
        return retVal;
    }

    private static ExtendedToken processConstantValues(ExtendedToken operand1, ExtendedToken operand2, String operator) throws WrongOperatorException {

        ExtendedToken tokenTrue = new ExtendedToken(CdqlConditionToken.newBuilder()
                .setType(CdqlConditionTokenType.Constant)
                .setStringValue("false")
                .setConstantTokenType(CdqlConstantConditionTokenType.Boolean)
                .build(), false);

        ExtendedToken tokenFalse = new ExtendedToken(CdqlConditionToken.newBuilder()
                .setType(CdqlConditionTokenType.Constant)
                .setStringValue("true")
                .setConstantTokenType(CdqlConstantConditionTokenType.Boolean)
                .build(), true);

        if (operand1.getCdqlConditionToken().getConstantTokenType() == CdqlConstantConditionTokenType.Numeric) {
            int res = Double.valueOf(operand1.getCdqlConditionToken().getStringValue())
                        .compareTo(Double.valueOf(operand2.getCdqlConditionToken().getStringValue()));
            switch (operator) {
                case ">":
                    return res > 0 ? tokenTrue : tokenFalse;
                case ">=":
                    return res >= 0 ? tokenTrue : tokenFalse;
                case "=":
                    return res == 0 ? tokenTrue : tokenFalse;
                case "<=":
                    return res <= 0 ? tokenTrue : tokenFalse;
                case "<":
                    return res < 0 ? tokenTrue : tokenFalse;
                case "!=":
                    return res != 0 ? tokenTrue : tokenFalse;
            }
        } else {
            switch (operator) {
                case "=":
                    return operand1.getCdqlConditionToken().getStringValue().equals(operand2.getCdqlConditionToken().getStringValue()) ?
                            tokenTrue : tokenFalse;
                case "!=":
                    return !operand1.getCdqlConditionToken().getStringValue().equals(operand2.getCdqlConditionToken().getStringValue()) ?
                            tokenTrue : tokenFalse;
            }
        }

        throw new WrongOperatorException("Wrong operator provided.");
    }

    private static Object tokenToString(CdqlConditionToken token) throws WrongOperatorException {
        switch (token.getType()) {
            case Attribute:
                return token.getContextAttribute().getPrefix();
            case Constant:
                switch (token.getConstantTokenType()) {
                    // This one is clear. If the token is a "constant" type, then read the value in string format
                    case Json:
                        return token.getStringValue().replaceAll("\"", "");
                    case Numeric:
                        return Double.valueOf(token.getStringValue().replaceAll("\"", ""));
                    case String:
                        return token.getStringValue().replaceAll("\"", "");
                    case Boolean:
                        return Boolean.parseBoolean(token.getStringValue());
                    default:
                        return token.getStringValue().replaceAll("\"", "");
                }
            default:
                throw new WrongOperatorException("The token " + token.getStringValue() + " with type " + token.getType().name()
                        + " is not accepted");
        }
    }

    private static JSONArray executeFunction(FunctionCall fCall, Map<String, JSONObject> ce, String operand, String value) throws Exception {
        switch(fCall.getFunctionName().toLowerCase()){
            case "distance":
                String resultEntity = null;

                String attribute = null;
                JSONArray appliee = null;
                JSONObject comparison = null;

                JSONArray result = new JSONArray();

                for(Operand op: fCall.getArgumentsList()){
                    ContextAttribute ca = op.getContextAttribute();
                    if(appliee == null) {
                        attribute = ca.getAttributeName();
                        resultEntity = ca.getEntityName();
                        appliee = ce.get(resultEntity).has("results") ?
                                ce.get(resultEntity).getJSONArray("results") : new JSONArray();
                    }
                    else {
                        JSONObject comparator = ce.get(ca.getEntityName());
                        if(comparator.has("results"))
                            comparison = new JSONObject(comparator.getJSONArray("results")
                                    .getJSONObject(0)
                                    .getString(ca.getAttributeName()));
                        else
                            comparison = (JSONObject) comparator.get(ca.getAttributeName());

                        double lat2 = comparison.getDouble("latitude");
                        double long2 = comparison.getDouble("longitude");

                        double distanceThreshold = 0.0;
                        if(value.startsWith("{")){
                            // TODO:
                            // Should convert any unit to meters
                            JSONObject val = new JSONObject(value);
                            distanceThreshold = Double.valueOf(val.getDouble("value"));
                        }
                        else
                            distanceThreshold = Double.valueOf(value);

                        for(Object resItem: appliee){
                            JSONObject item = new JSONObject(((JSONObject) resItem).getString(attribute));
                            if(isSatidfied(distance(item.getDouble("latitude"),
                                    item.getDouble("longitude"),lat2,long2), operand, distanceThreshold)){
                                result.put((JSONObject)resItem);
                            }
                        }
                    }
                }
                return result;
        }
        return null;
    }

    private static boolean isSatidfied(double distance, String operator, double value) throws WrongOperatorException {
        switch(operator){
            case "=": return distance == value;
            case "!=": return distance != value;
            case ">": return distance > value;
            case ">=": return distance >= value;
            case "<": return distance < value;
            case "<=": return distance <= value;
            default:
                log.log(Level.INFO, "{0} not supported with Distance function", operator);
                String result = "Only < (less than) is supported with Distance function";
                throw new WrongOperatorException(result);
        }
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2)
    {
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        // Radius of the earth in meters.
        double c = 2 * Math.asin(Math.sqrt(a));
        double r = 6371000;
        return(c * r);
    }

    public static Object executeFunctionCall(FunctionCall fCall, Map<String, JSONObject> ce, CDQLQuery query) {
        FunctionCall.Builder fCallTemp = FunctionCall.newBuilder().setFunctionName(fCall.getFunctionName());
        fCallTemp.addAllSubItems(fCall.getSubItemsList());

        for (Operand argument : fCall.getArgumentsList()) {
            if (null != argument.getType()) {
                switch (argument.getType()) {
                    case CONTEXT_ATTRIBUTE:
                        ContextAttribute contextAttribute = argument.getContextAttribute();
                        JSONObject entity = ce.get(contextAttribute.getEntityName());
                        String[] split = contextAttribute.getAttributeName().split("\\.");
                        JSONObject item = entity;
                        for (int i = 0; i < split.length - 1; i++) {
                            item = item.getJSONObject(split[i]);
                        }
                        String value = String.valueOf(item.get(split[split.length - 1]));
                        fCallTemp.addArguments(Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_STRING).setStringValue(value));
                        break;
                    case CONTEXT_ENTITY:
                        String entityID = argument.getContextEntity().getEntityID();
                        JSONObject operandEntity = ce.get(entityID);
                        ContextEntity findEntity = query.getDefine().getDefinedEntitiesList().stream().filter(i -> i.getEntityID().equals(entityID)).findFirst().get();
                        String entityTypeString = findEntity.getType().toString();
                        operandEntity.put("entityType", entityTypeString);
                        fCallTemp.addArguments(Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_JSON).setStringValue(operandEntity.toString()));
                        break;
                    case FUNCTION_CALL:
                        fCallTemp.addArguments(Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_JSON).setStringValue(executeFunctionCall(argument.getFunctioncall(), ce, query).toString()));
                        break;
                    default:
                        fCallTemp.addArguments(argument);
                        break;
                }
            }
        }
        if (fCall.getFunctionName().equals("avg")) {
            return executeAggregationFunction(fCallTemp.build());
        } else if (fCall.getFunctionName().equals("cluster")) {
            return groupByLocation(fCallTemp.build());
        } else if (fCall.getFunctionName().equals("now")) {
            return now(fCallTemp.build());
        } else if (fCall.getFunctionName().equals("embed")) {
            return embedSituation(fCallTemp.build(), ce, query);
        } else {
            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            SituationFunction situationFunction = sqemStub.findSituationByTitle(SituationFunctionRequest.newBuilder().setName(fCall.getFunctionName()).build());

            return executeSituationFunction(situationFunction, fCallTemp.build());

//            if (execute.trim().startsWith("[")) {
//                return new JSONArray(execute.replaceAll("\"", ""));
//            } else {
//                return execute;
//            }
        }
    }

    // Executes the aggregation on the values retrieved for the entity
    private static JSONObject executeAggregationFunction(FunctionCall fCall) {
        List<Operand> arguments = fCall.getArgumentsList();

        JSONObject result = new JSONObject();
        Map<String, Integer> numberOfInstances = new HashMap<>();

        for (Operand argument : arguments) {
            if (argument.getType().equals(OperandType.CONTEXT_VALUE_JSON)) {
                JSONObject entity = new JSONObject(argument.getStringValue());
                result.put("entityType", entity.opt("entityType"));
                if (entity.has("results")) {
                    JSONArray items = entity.getJSONArray("results");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        result = sum(result, item, numberOfInstances);
                    }
                    result = computeAverage(result, numberOfInstances);
                }
            }
        }

        return result;
    }

    private static Long now(FunctionCall fCall) {
        Long offset = Long.valueOf(fCall.getArguments(0).getStringValue().replaceAll("\"", ""));
        return System.currentTimeMillis() + offset;
    }

    // H3 is already used here to group a set of locations. Basically, the location indexes exist.
    private static JSONObject groupByLocation(FunctionCall fCall) {
        JSONArray finalResult = new JSONArray();
        Map<String, List<JSONObject>> cluster = new HashMap<>();
        JSONObject jo = new JSONObject(fCall.getArguments(0).getStringValue());
        JSONArray entities = jo.getJSONArray("results");

        int res = Integer.valueOf(fCall.getArguments(1).getStringValue());

        H3Core h3 = null;
        try {
            h3 = H3Core.newInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < entities.length(); i++) {
            JSONObject entity = entities.getJSONObject(i);
            if (!entity.has("geo")) {
                continue;
            }
            JSONObject geo = entity.getJSONObject("geo");
            String h3Id = h3.geoToH3Address(geo.getDouble("latitude"), geo.getDouble("longitude"), res);
            if (cluster.containsKey(h3Id)) {
                cluster.get(h3Id).add(entity);
            } else {
                List<JSONObject> tempList = new ArrayList<>();
                tempList.add(entity);
                cluster.put(h3Id, tempList);
            }
        }

        for (Map.Entry<String, List<JSONObject>> entry : cluster.entrySet()) {
            JSONObject result = new JSONObject();
            Map<String, Integer> numberOfInstances = new HashMap<>();
            JSONObject geo = new JSONObject();
            geo.put("type", "Point");
            GeoCoord geoCoord = h3.h3ToGeo(entry.getKey());
            geo.put("latitude", geoCoord.lat);
            geo.put("longitude", geoCoord.lng);
            JSONArray coordinates = new JSONArray();
            coordinates.put(geoCoord.lng);
            coordinates.put(geoCoord.lat);
            geo.put("coordinates", coordinates);
            for (JSONObject item :
                    entry.getValue()) {
                result = sum(result, item, numberOfInstances);
            }
            result = computeAverage(result, numberOfInstances);
            result.put("geo", geo);
            result.put("identifier", entry.getKey());
            result.put("entityType", jo.get("entityType"));
            finalResult.put(result);
        }
        JSONObject objectResult = new JSONObject();
        objectResult.put("entityType", jo.get("entityType"));
        objectResult.put("results", finalResult);
        return objectResult;
    }

    private static JSONObject embedSituation(FunctionCall fCall, Map<String, JSONObject> ce, CDQLQuery query) {

        JSONObject jo = new JSONObject(fCall.getArguments(0).getStringValue());
        JSONArray entities = jo.getJSONArray("results");
        String entityType = jo.get("entityType").toString();
        String functionName = fCall.getArguments(1).getStringValue().replaceAll("\"", "");
        String[] names = functionName.split("\\.");
        functionName = names[0];

        for (int i = 0; i < entities.length(); i++) {
            JSONObject entity = entities.getJSONObject(i);
            entity.put("entityType", entityType);
            FunctionCall.Builder fCallTemp = FunctionCall.newBuilder().setFunctionName(functionName);
            for (int j = 1; j < names.length; j++) {
                fCallTemp.addSubItems(names[j]);
            }
            fCallTemp.addArguments(Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_JSON).setStringValue(entity.toString()));
            entity.put(functionName, executeFunctionCall(fCallTemp.build(), ce, query));
        }

        JSONObject objectResult = new JSONObject();
        objectResult.put("results", entities);
        return objectResult;
    }

    private static JSONObject sum(JSONObject jo1, JSONObject jo2, Map<String, Integer> numberOfInstances) {
        for (String key : jo2.keySet()) {
            Object item = jo2.get(key);
            Object source = jo1.opt(key);

            if (numberOfInstances.containsKey(key)) {
                numberOfInstances.put(key, numberOfInstances.get(key) + 1);
            } else {
                numberOfInstances.put(key, 1);
            }
            try {
                Double v1 = Double.valueOf(item.toString());
                if (source != null) {
                    jo1.put(key, v1 + Double.valueOf(source.toString()));
                } else {
                    jo1.put(key, v1);
                }
            } catch (Exception e) {
                if (key.equals("entityType")) {
                    if (source != null) {
                        jo1.put(key, item);
                    }
                } else if (item instanceof JSONObject) {
                    if (source != null) {
                        jo1.put(key, sum((JSONObject) source, (JSONObject) item, numberOfInstances));
                    } else {
                        jo1.put(key, item);
                    }
                } else if (item instanceof String) {
                    if (source != null) {
                        if (source.equals(item)) {

                        } else if (source instanceof JSONArray) {
                            ((JSONArray) source).put(item);
                            jo1.put(key, source);
                        } else {
                            JSONArray ja = new JSONArray();
                            ja.put(source);
                            ja.put(item);
                            jo1.put(key, ja);
                        }
                    } else {
                        jo1.put(key, item);
                    }
                }
            }
        }
        return jo1;
    }

    private static JSONObject computeAverage(JSONObject jo2, Map<String, Integer> numberOfInstances) {
        for (String key : jo2.keySet()) {
            Object item = jo2.get(key);
            try {
                Double v1 = Double.valueOf(item.toString());
                jo2.put(key, v1 / numberOfInstances.get(key));
            } catch (Exception e) {
                if (item instanceof JSONObject) {
                    jo2.put(key, computeAverage((JSONObject) item, numberOfInstances));
                }
            }
        }
        return jo2;
    }

    public static Object executeSituationFunction(SituationFunction function, FunctionCall fCall) {

        CREServiceGrpc.CREServiceBlockingStub creStub
                = CREServiceGrpc.newBlockingStub(CREChannel.getInstance().getChannel());

        List<Operand> arguments = fCall.getArgumentsList();

        Map<String, ContextEntityType> tempList = new LinkedHashMap(function.getRelatedEntitiesMap());

        List<List<AttributeValue>> allAttributeValues = new ArrayList<>();
        for (Operand argument : arguments) {
            List<List<AttributeValue>> attributeValues = new ArrayList<>();
            String entityKey = null;
            if (argument.getType().equals(OperandType.CONTEXT_VALUE_JSON)) {
                JSONObject entities = new JSONObject(argument.getStringValue());
                if (entities.has("results")) {
                    JSONArray results = entities.getJSONArray("results");
                    String entityType = entities.getString("entityType");
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject entity = results.getJSONObject(i);

                        Map.Entry<String, ContextEntityType> findEntity = tempList.entrySet().stream().filter(p -> p.getValue().toString().equals(entityType)).findFirst().get();
                        String prefix = findEntity.getKey();
                        entityKey = findEntity.getKey();
                        List<AttributeValue> entityAttributeValues = new ArrayList<>();
                        for (String key : entity.keySet()) {
                            if (function.getAllAttributesList().contains(prefix + "." + key)) {
                                Object get = entity.get(key);
                                String value = get.toString();
                                if (get instanceof JSONObject) {
                                    value = ((JSONObject) get).get("value").toString();
                                }
                                entityAttributeValues.add(AttributeValue.newBuilder().setAttributeName(prefix + "." + key).setValue(value).build());
                            }
                        }
                        attributeValues.add(entityAttributeValues);
                    }
                } else {
                    String entityType = entities.getString("entityType");
                    Map.Entry<String, ContextEntityType> findEntity = tempList.entrySet().stream().filter(p -> p.getValue().getType().equals(entityType)).findFirst().get();
                    String prefix = findEntity.getKey();
                    entityKey = findEntity.getKey();
                    List<AttributeValue> entityAttributeValues = new ArrayList<>();
                    for (String key : entities.keySet()) {
                        if (function.getAllAttributesList().contains(prefix + "." + key)) {
                            Object get = entities.get(key);
                            String value = get.toString();
                            if (get instanceof JSONObject) {
                                value = ((JSONObject) get).get("value").toString();
                            }
                            entityAttributeValues.add(AttributeValue.newBuilder().setAttributeName(prefix + "." + key).setValue(value).build());
                        }
                    }
                    attributeValues.add(entityAttributeValues);
                }
                tempList.remove(entityKey);
            }

            if (allAttributeValues.isEmpty()) {
                allAttributeValues.addAll(attributeValues);
            } else {
                for (int j = 0; j < allAttributeValues.size(); j++) {
                    for (List<AttributeValue> av : attributeValues
                    ) {
                        allAttributeValues.get(j).addAll(av);
                    }
                }
            }
        }

        JSONArray finalResult = new JSONArray();
        for (List<AttributeValue> attributeValue :
                allAttributeValues) {
            JSONObject itemResult = new JSONObject();
            SituationInferenceRequest.Builder request = SituationInferenceRequest.newBuilder();
            request.addAllSituationDescriptions(function.getSituationsList());
            request.addAllAttributeValues(attributeValue);
            JSONArray avs = new JSONArray();
            for (AttributeValue av :
                    attributeValue) {
                JSONObject jo = new JSONObject();
                jo.put(av.getAttributeName(), av.getValue());
                avs.put(jo);
            }
            itemResult.put("attributeValues", avs);
            try {
                CREResponse creResponse = creStub.infer(request.build());
                if (fCall.getSubItemsList().isEmpty()) {
                    JSONObject jo = new JSONObject();
                    for (ReasoningResponse rr : creResponse.getBodyList()) {
                        jo.put(rr.getSituationTitle(), rr.getConfidence());
                    }
                    itemResult.put("outcome", jo);
                } else {
                    String situation = fCall.getSubItemsList().get(0);
                    Optional<ReasoningResponse> rr = creResponse.getBodyList().stream().filter(p -> p.getSituationTitle().equalsIgnoreCase(situation)).findFirst();
                    if (rr.isPresent()) {
                        itemResult.put("outcome", rr.get().getConfidence());
                    } else {
                        itemResult.put("outcome", 0);
                    }
                }
            } catch (Exception ex) {
                log.log(Level.SEVERE, null, ex);
            }
            finalResult.put(itemResult);
        }
        if (finalResult.length() == 1) {
            // This is a quick fix
            Object outcome = finalResult.getJSONObject(0).get("outcome");
            if(outcome instanceof JSONObject){
                String situationName = function.getSituations(0).getSituationName();
                return ((JSONObject) outcome).get(situationName);
            }
            return outcome;
            // return finalResult.getJSONObject(0).get("outcome");
        } else {
            JSONObject resultWrapper = new JSONObject();
            resultWrapper.put("results", finalResult);
            return resultWrapper;
        }
    }

    private static AbstractMap.SimpleEntry retrieveContext(ContextEntity targetEntity, String contextServicesText,
                                                           HashMap<String,String> params, int limit, String criticality,
                                                           JSONObject consumerSLA, double complexity, Set<String> attributes) {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        // This list is currently unordered.
        JSONArray contextServices = new JSONArray(contextServicesText);

        // This for loop returns the context data from the first context service.
        // Although, there may be multiple other context services, this loop assumes the context services are ordered.
        // Therefore, the 1st SC in the list is the best match (Input from Kanaka).
        for (Object ctSer : contextServices) {
            // Mapping context service with SLA constraints
            JSONObject qos = consumerSLA.getJSONObject("sla").getJSONObject("qos");
            JSONObject conSer = mapServiceWithSLA((JSONObject) ctSer, qos, targetEntity.getType(), criticality.toLowerCase());

            if(conSer.getJSONObject("sla").getBoolean("cache") && cacheEnabled){
                JSONArray candidate_keys = conSer.getJSONObject("sla").getJSONArray("key");
                String keys = "";
                for(Object k : candidate_keys)
                    keys = keys.isEmpty() ? k.toString() : keys + "," + k.toString();

                JSONObject slaObj = conSer.getJSONObject("sla");

                // By this line, I should have completed identifying the query class
                CacheLookUp.Builder lookup = CacheLookUp.newBuilder().putAllParams(params)
                        .setEt(targetEntity.getType())
                        .setServiceId(conSer.getJSONObject("_id").getString("$oid"))
                        .setCheckFresh(true)
                        .setKey(keys).setQClass("1") // TODO: Assign the correct context query class
                        .setUniformFreshness(slaObj.getJSONObject("freshness").toString()) // current lifetime & REQUESTED fthresh for the query
                        .setSamplingInterval(slaObj.getJSONObject("updateFrequency").toString()); // sampling

                // Look up also considers known refreshing logics
                SQEMResponse data = sqemStub.handleContextRequestInCache(lookup.build());

                if(data.getBody().equals("") && data.getStatus() != "500"){
                    // Need to fetch from a different Context Provider for 2 scenarios when partial misses occur when proactively refreshed, and
                    // (1) The data are streamed
                    // (2) The sensor is sampling at a certain rate
                    if(data.getStatus().equals("400") && data.getMeta().equals("proactive_shift")
                            && slaObj.getJSONObject("updateFrequency").getDouble("value") > 0)
                        continue;

                    // Fetching from the same context provider OR it's stream otherwise
                    String retEntity = slaObj.getBoolean("autoFetch") ?
                            RetrievalManager.executeFetch(conSer.toString(), slaObj.getJSONObject("qos"), params,
                                    conSer.getJSONObject("_id").getString("$oid"),
                                    consumerSLA.getJSONObject("_id").getString("$oid"),
                                    targetEntity.getType().getType(), attributes) :
                            RetrievalManager.executeStreamRead(conSer.toString(), slaObj.getJSONObject("qos"), params);

                    // There is problem with the current context provider which makes it unsuitable for retrieving now.
                    // Therefore, has to move to the next context provider
                    if(retEntity == null) continue;

                    Executors.newCachedThreadPool().execute(()
                            -> refreshOrCacheContext(slaObj, Integer.parseInt(data.getStatus()), lookup,
                            retEntity, data.getMeta(), complexity, attributes));
                    return new AbstractMap.SimpleEntry(retEntity,qos.put("price",
                            consumerSLA.getJSONObject("sla").getJSONObject("price").getDouble("value")));
                }
                else {
                    return new AbstractMap.SimpleEntry(data.getBody(),
                            qos.put("price", consumerSLA.getJSONObject("sla").getJSONObject("price").getDouble("value")));
                }
            }

            JSONObject slaObj = conSer.getJSONObject("sla");
            String retEntity = conSer.getJSONObject("sla").getBoolean("autoFetch") ?
                    RetrievalManager.executeFetch(conSer.toString(), slaObj.getJSONObject("qos"), params,
                            conSer.getJSONObject("_id").getString("$oid"),
                            consumerSLA.getJSONObject("_id").getString("$oid"),
                            targetEntity.getType().getType(), attributes) :
                    RetrievalManager.executeStreamRead(conSer.toString(), slaObj.getJSONObject("qos"), params);
            if(retEntity != null)
                return new AbstractMap.SimpleEntry(retEntity,
                        qos.put("price", consumerSLA.getJSONObject("sla").getJSONObject("price").getDouble("value")));
            else continue; // Moving to the next context provider since it is currently unavailable.
        }

        log.severe("Runtime error during context retrieval.");
        return null;
    }

    private static void refreshOrCacheContext(JSONObject sla, int cacheStatus, CacheLookUp.Builder lookup,
                                              String retEntity, String currRefPolicy, double complexity,
                                              Set<String> attributes){
        CPREEServiceGrpc.CPREEServiceFutureStub asynStub
                = CPREEServiceGrpc.newFutureStub(CPREEChannel.getInstance().getChannel());

        if(cacheStatus == 400){
            // 400 means the cache missed due to invalidity
            // The cached context needs to be refreshed.
            asynStub.refreshContext(ContextRefreshRequest.newBuilder()
                    .addAllAttributes(attributes)
                    .setRefreshPolicy(currRefPolicy)
                    .setRequest(CacheRefreshRequest.newBuilder()
                            .setSla(sla.toString())
                            .setReference(lookup)
                            .setJson(retEntity)).build());
        }
        else if(cacheStatus == 404 && registerState){
            // 404 means the item is not at all cached
            // Trigger Selective Caching Evaluation
            asynStub.evaluateAndCacheContext(CacheSelectionRequest.newBuilder()
                            .setSla(sla.toString())
                            .setContext(retEntity)
                            .setContextLevel(CacheLevels.RAW_CONTEXT.toString().toLowerCase())
                            .setReference(lookup)
                            .setComplexity(complexity)
                            .addAllAttributes(attributes)
                            .build());
        }
    }

    private static JSONObject mapServiceWithSLA(JSONObject conSer, JSONObject qos, ContextEntityType etype, String criticality){
        Double fthresh = -1.0;

        if(qos.getBoolean("staged") && qos.getJSONObject("criticality").has(criticality)){
            JSONObject crit = qos.getJSONObject("criticality").getJSONObject(criticality);

            if(crit.has("fthresh")){
                JSONArray conList = crit.getJSONArray("fthresh");
                for(int i = 0; i<conList.length(); i++){
                    if(conList.getJSONObject(i).getString("@context").equals(etype.getVocabURI()) &&
                            conList.getJSONObject(i).getString("@type").equals(etype.getType())){
                        fthresh = conList.getJSONObject(i).getDouble("value");
                    }
                }
            }

            if(fthresh < 0){
                fthresh = qos.getDouble("fthresh");
            }
        }
        else
            fthresh = qos.getDouble("fthresh");

        conSer.getJSONObject("sla")
                .getJSONObject("freshness")
                .put("fthresh", fthresh);

        return conSer;
    }

    public static ContextRequest generateContextRequest(ContextEntity targetEntity, CDQLQuery query, Map<String, JSONObject> ce, int page, int limit) {
        ArrayList<CdqlConditionToken> list = new ArrayList(targetEntity.getCondition().getRPNConditionList());

        for (int i = 0; i < list.size(); i++) {
            CdqlConditionToken item = list.get(i);

            switch (item.getType()) {
                case Function:
                    FunctionCall.Builder fCall = item.getFunctionCall().toBuilder();
                    FunctionCall.Builder fCallTemp = FunctionCall.newBuilder().setFunctionName(fCall.getFunctionName());
                    if (fCall.getFunctionName().toLowerCase().equals("distance") || fCall.getFunctionName().toLowerCase().equals("geoinside") || fCall.getFunctionName().toLowerCase().equals("geoinsidebox")) {
                        continue;
                    }
                    fCallTemp.addAllSubItems(fCall.getSubItemsList());
                    int requiredArgs = fCall.getArgumentsList().size();
                    for (int j = 0; j < fCall.getArgumentsList().size(); j++) {
                        Operand argument = fCall.getArgumentsList().get(j);
                        if (argument.getType() == OperandType.CONTEXT_ENTITY) {
                            ContextEntity object = argument.getContextEntity();
                            String entityID = object.getEntityID();
                            if (!entityID.equals(targetEntity.getEntityID())) {
                                ContextEntity findEntity = query.getDefine().getDefinedEntitiesList().stream().filter(e -> e.getEntityID().equals(entityID)).findFirst().get();
                                String entityTypeString = findEntity.getType().getType();
                                JSONObject operandEntity = ce.get(entityID);
                                operandEntity.put("entityType", entityTypeString);
                                String handelQuantitativeValue = handelQuantitativeValue(operandEntity.toString());
                                fCallTemp.addArguments(j, Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_JSON).setStringValue(handelQuantitativeValue));
                                requiredArgs--;
                            }

                        } else if (argument.getType() == OperandType.CONTEXT_ATTRIBUTE) {
                            ContextAttribute ca = argument.getContextAttribute();
                            String entityNameTemp = ca.getEntityName();
                            String attributeNameTemp = ca.getAttributeName();
                            if (!entityNameTemp.equals(targetEntity.getEntityID())) {
                                String handelQuantitativeValue = handelQuantitativeValue(ce.get(entityNameTemp).get(attributeNameTemp).toString());
                                fCallTemp.addArguments(j, Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_JSON).setStringValue(handelQuantitativeValue));
                                requiredArgs--;
                            }
                        } else if (argument.getType() == OperandType.FUNCTION_CALL) {
                            //toDo
                        } else {
                            fCallTemp.addArguments(j, Operand.newBuilder().setType(argument.getType()).setStringValue(argument.getStringValue()));
                            requiredArgs--;
                        }
                    }
                    if (requiredArgs == 0) {
                        String val = executeFunctionCall(fCallTemp.build(), ce, query).toString();
                        CdqlConstantConditionTokenType constantType = getConstantType(val);
                        CdqlConditionToken constantToken = CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Constant)
                                .setStringValue(val)
                                .setConstantTokenType(constantType).build();
                        list.set(i, constantToken);
                    }
                    break;
                case Attribute: {
                    String valueEntityID = item.getContextAttribute().getEntityName();
                    if (!valueEntityID.equals(targetEntity.getEntityID()) && ce.containsKey(valueEntityID)) {

                        String val = handelQuantitativeValue(extractValueFromEntity(ce.get(valueEntityID), item.getContextAttribute().getAttributeName()));
                        //     ce.get(valueEntityID).get(caConditionToken.getContextAttribute().getAttributeName()).toString());
                        CdqlConstantConditionTokenType constantType = getConstantType(val);

                        CdqlConditionToken constantToken = CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Constant)
                                .setStringValue(val)
                                .setConstantTokenType(constantType).build();
                        list.set(i, constantToken);
                    }
                    break;
                }
                case Entity: {
                    String valueEntityID = item.getContextEntity().getEntityID();

                    if (!valueEntityID.equals(targetEntity.getEntityID()) && ce.containsKey(valueEntityID)) {
                        String val = handelQuantitativeValue(ce.get(valueEntityID).toString());
                        CdqlConstantConditionTokenType constantType = getConstantType(val);
                        CdqlConditionToken constantToken = CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Constant)
                                .setStringValue(val)
                                .setConstantTokenType(constantType).build();
                        list.set(i, constantToken);
                    }
                }
            }
        }
        ContextEntity.Builder tempContextEntityBuilder = ContextEntity.newBuilder();
        tempContextEntityBuilder.getConditionBuilder().addAllRPNCondition(list);
        tempContextEntityBuilder.setEntityID(targetEntity.getEntityID());
        tempContextEntityBuilder.setType(targetEntity.getType());
        ContextEntity tempContextEntity = tempContextEntityBuilder.build();
        List<ContextAttribute> returnAttributes = new ArrayList<>();
        //ToDo fix return attribute section
        ContextRequest cr = ContextRequest.newBuilder().setEt(targetEntity.getType())
                .setCondition(tempContextEntity.getCondition())
                .setMeta(query.getMeta())
                .setPage(page)
                .setLimit(limit)
                .setEntityID(targetEntity.getEntityID())
                .addAllReturnAttributes(returnAttributes).build();

        return cr;
    }

    public static JSONObject executeSQEMQuery(ContextEntity targetEntity, CDQLQuery query,
                                                           Map<String, JSONObject> ce, int page, int limit) {
        AbstractMap.SimpleEntry res = executeSQEMQuery(targetEntity, query, ce, page, limit, null);
        return (JSONObject) res.getKey();
    }

    private static AbstractMap.SimpleEntry executeSQEMQuery(ContextEntity targetEntity, CDQLQuery query,
                                               Map<String, JSONObject> ce, int page, int limit, JSONObject sla) {
        ContextRequest cr = generateContextRequest(targetEntity, query, ce, page, limit);

        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        JSONObject qos = sla.getJSONObject("sla").getJSONObject("qos");

        Iterator<Chunk> data = sqemStub.handleContextRequest(cr);

        ByteString.Output output = ByteString.newOutput();

        data.forEachRemaining(c -> {
            output.write(c.getData().toByteArray(), c.getIndex() * MAX_MESSAGE_SIZE, c.getData().size());
        });

        JSONObject invoke = new JSONObject();

        try {
            SQEMResponse sqemResponse = SQEMResponse.parseFrom(output.toByteString());
            if (sqemResponse.getMeta() != null) {
                invoke.put("results", new JSONArray(sqemResponse.getBody()));
                invoke.put("meta", new JSONObject(sqemResponse.getMeta()));
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        //ToDo: validate return entities
        if(sla == null){
            return new AbstractMap.SimpleEntry(invoke,null);
        }

        return new AbstractMap.SimpleEntry(invoke,
                qos.put("price", sla.getJSONObject("sla").getJSONObject("price").getDouble("value")));
    }

    private static CdqlConstantConditionTokenType getConstantType(String val) {

        CdqlConstantConditionTokenType constantType = CdqlConstantConditionTokenType.String;
        if (val.startsWith("{")) {
            constantType = CdqlConstantConditionTokenType.Json;
        } else if (val.startsWith("[")) {
            constantType = CdqlConstantConditionTokenType.Array;
        } else {
            try {
                Double.valueOf(val);
                constantType = CdqlConstantConditionTokenType.Numeric;
            } catch (NumberFormatException e) {

            }
        }

        return constantType;
    }

    private static String extractValueFromEntity(JSONObject entity, String attributeName) {
        if (entity.has("results")) {
            JSONArray ja = entity.getJSONArray("results");
            if (ja.length() == 1) {
                return ja.getJSONObject(0).get(attributeName).toString();
            }
            JSONArray res = new JSONArray();
            for (int i = 0; i < ja.length(); i++) {
                JSONObject item = ja.getJSONObject(i);

                Object subitem = item.get(attributeName);
                if (subitem instanceof JSONArray) {
                    JSONArray ja2 = (JSONArray) subitem;
                    for (int j = 0; j < ja2.length(); j++) {
                        res.put(ja2.get(j).toString());
                    }
                } else {
                    res.put(subitem.toString());
                }
            }
            return res.toString();
        } else {
            return entity.get(attributeName).toString();
        }
    }

    private static String handelQuantitativeValue(String value) {
        if (!value.startsWith("{")) {
            return value;
        }
        value = value.replaceAll("\"type\"", "\"@type\"");
        JSONObject jsonValue = new JSONObject(value);
        if (!jsonValue.has("@type")) {
            return value;
        }
        if (!jsonValue.getString("@type").equalsIgnoreCase("QuantitativeValue")) {
            return value;
        }

        String origUnit = jsonValue.getString("unitCode");
        AbstractMap.SimpleEntry unitProp = Utilities.getDefaultUnitCode(origUnit);
        if(((String) unitProp.getKey()).equals(origUnit))
            return jsonValue.getString("value");

        return Utilities.unitConverter((MeasuredProperty) unitProp.getValue(), origUnit,
                (String) unitProp.getKey(), Double.valueOf(jsonValue.getString("value"))).toString();
    }
}
