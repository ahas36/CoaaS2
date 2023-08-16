package au.coaas.cqc.executor;

import au.coaas.base.proto.ListOfString;

import au.coaas.cpree.proto.SimpleContainer;
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
import java.util.concurrent.ExecutorService;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.stream.Stream;
import java.util.stream.Collectors;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

public class PullBasedExecutor {

    // This is the cache switch
    private static boolean cacheEnabled = true;
    private static boolean registerState = false;

    private static final int numberOfThreads = 10;
    private static final int numberOfItemsPerTask = 10;

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
                                                    String queryId, String criticality, double complexity,
                                                    List<ContextEntity> subEntities) throws Exception{

        // Initialize values
        Map<String, JSONObject> ce = new HashMap<>();
        JSONObject consumerQoS = null;

        // Iterates over execution plan.
        // The first loop goes over dependent sets of entities.
        Collection<ListOfString> aList = query.getExecutionPlanMap().values();

        // Fetching Consumer SLA
        JSONObject sla = null;
        SQEMServiceGrpc.SQEMServiceFutureStub sqemStub
                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
        ListenableFuture<SQEMResponse> slaMessage = sqemStub.getConsumerSLA(AuthToken.newBuilder().setToken(authToken).build());

        // Replace with bList for Testing
        for (ListOfString entityList : aList) {
            // Second loop iterates over the entities in the dependent set.
            for (String entityID : entityList.getValueList()) {
                ContextEntity entity = query.getDefine().getDefinedEntitiesList().stream().filter(v -> v.getEntityID().equals(entityID)).findFirst().get();
                // Here the entity ID is the aliase that is defined in the query. e.g., e1 in "define entity e1 is from swm:type1".

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
                    ContextRequest.Builder contextRequest = generateContextRequest(entity, query, ce, 0, 0, complexity);
                    List<CdqlConditionToken> rpnConditionList = contextRequest.getCondition().getRPNConditionList();

                    String key = null;
                    String value = null;

                    LinkedHashMap<String,String> params = new LinkedHashMap();
                    List<String> operators = new ArrayList<>();

                    LinkedList<CdqlConditionTokenType> proceeded = new LinkedList<>();
                    for (CdqlConditionToken cdqlConditionToken : rpnConditionList) {
                        if(cdqlConditionToken.getType() != CdqlConditionTokenType.Operator){
                            proceeded.add(cdqlConditionToken.getType());
                        }

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
                            case Operator:
                                int size = proceeded.size();
                                if(size>=2){
                                    CdqlConditionTokenType opnd_1 = proceeded.get(size - 1);
                                    CdqlConditionTokenType opnd_2 = proceeded.get(size - 2);
                                    boolean ifAtt_1 = opnd_1 == CdqlConditionTokenType.Attribute || opnd_1 == CdqlConditionTokenType.Constant;
                                    boolean ifAtt_2 = opnd_2 == CdqlConditionTokenType.Attribute || opnd_2 == CdqlConditionTokenType.Constant;
                                    if(ifAtt_1 && ifAtt_2) {
                                        operators.add(cdqlConditionToken.getStringValue());
                                        proceeded.clear();
                                    }
                                }
                        }
                    }

                    if(sla == null) {
                        SQEMResponse slaResult = slaMessage.get();
                        if(slaResult.getStatus().equals("200")){
                            sla = new JSONObject(slaResult.getBody());
                            JSONObject finalSla = sla;
                            // Profiling the context consumers
                            Executors.newCachedThreadPool().submit(() -> profileConsumer(finalSla,queryId));
                        }
                    }

                    // Checking if context provider subscriptions need to be done.
                    ContextEntity subscribeEntity = null;
                    if(subEntities != null){
                        Optional<ContextEntity> tempEnt = subEntities.stream()
                                .filter(v -> v.getEntityID().equals(entityID)).findFirst();
                        if(tempEnt.isPresent()) subscribeEntity = tempEnt.get();
                    }

                    AbstractMap.SimpleEntry cacheResult = retrieveContext(entity, contextService, params, limit,
                                                                    criticality, sla, complexity, terms.keySet(),
                                                                    operators, subscribeEntity);

                    // Checking if from Stream Read or not.
                    if(cacheResult != null){
                        if(cacheResult.getKey() != null){
                            if(cacheResult.getKey().toString().startsWith("{")){
                                JSONObject resultJson = new JSONObject((String) cacheResult.getKey());
                                ce.put(entity.getEntityID(),resultJson);
                            }
                            else {
                                JSONArray resultJson = new JSONArray((String) cacheResult.getKey());
                                ce.put(entity.getEntityID(),resultJson.getJSONObject(0));
                            }
                            if(query.getSelect().getSelectAttrsMap().containsKey(entity.getEntityID()))
                                executeQueryBlock(rpnConditionList, ce, entityID);
                            if(consumerQoS == null)  consumerQoS = (JSONObject) cacheResult.getValue();
                        }
                        else {
                            AbstractMap.SimpleEntry rex = executeSQEMQuery(entity, query, ce, page, limit,
                                    ((SimpleContainer) cacheResult.getValue()).getContextService(), complexity);
                            ce.put(entity.getEntityID(), (JSONObject) rex.getKey());

                            if(cacheEnabled){
                                if(consumerQoS == null) {
                                    consumerQoS = sla.getJSONObject("sla").getJSONObject("qos");
                                }

                                JSONObject finalConsumerQoS = consumerQoS;
                                Executors.newCachedThreadPool().submit(() -> {
                                    JSONObject conSer = new JSONObject(
                                            ((SimpleContainer) cacheResult.getValue()).getContextService());
                                    JSONArray candidate_keys = conSer.getJSONObject("sla").getJSONArray("key");
                                    String keys = "";
                                    for(Object k : candidate_keys)
                                        keys = keys.isEmpty() ? k.toString() : keys + "," + k.toString();

                                    JSONObject freshnessCal = conSer.getJSONObject("sla").getJSONObject("freshness");
                                    freshnessCal.put("fthresh", finalConsumerQoS.getDouble("fthresh"));

                                    CacheLookUp.Builder lookup = CacheLookUp.newBuilder().putAllParams(params)
                                            .setEt(entity.getType())
                                            .setServiceId(conSer.getJSONObject("_id").getString("$oid"))
                                            .setCheckFresh(true)
                                            .setKey(keys).setQClass("1")
                                            .setUniformFreshness(freshnessCal.toString()) // current lifetime & REQUESTED fthresh for the query
                                            .setSamplingInterval(conSer.getJSONObject("sla")
                                                    .getJSONObject("updateFrequency").toString()); // sampling

                                    SimpleContainer summary = (SimpleContainer) cacheResult.getValue();
                                    refreshOrCacheContext(
                                            conSer.getJSONObject("sla"),
                                            Integer.parseInt(summary.getStatus()),
                                            lookup, rex.getKey().toString(),
                                            summary.getRefPolicy(),
                                            complexity, summary.getHashKeysList());
                                });
                            }
                        }
                    }
                    else {
                        JSONObject resultMessage = new JSONObject();
                        resultMessage.put("Message", "One or more context requested can not be accessed!");

                        CdqlResponse cdqlResponse = CdqlResponse.newBuilder().setStatus("400")
                                .setQueryId(queryId)
                                .setBody(resultMessage.toString())
                                .setAdmin(CdqlAdmin.newBuilder()
                                        .setRtmax(consumerQoS.getJSONObject("rtmax").getLong("value"))
                                        .setPrice(consumerQoS.has("price")? consumerQoS.getDouble("price") :
                                                sla.getJSONObject("sla").getJSONObject("price").getDouble("value"))
                                        .setRtpenalty(consumerQoS.getJSONObject("rtmax").getJSONObject("penalty")
                                                .getDouble("value"))
                                        .build())
                                .build();

                        return cdqlResponse;
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
                            Executors.newCachedThreadPool().submit(() -> profileConsumer(finalSla,queryId));
                        }
                    }

                    AbstractMap.SimpleEntry rex = executeSQEMQuery(entity, query, ce, page, limit, null, complexity);
                    ce.put(entity.getEntityID(), (JSONObject) rex.getKey());
                    if(consumerQoS == null) {
                        consumerQoS = sla.getJSONObject("sla").getJSONObject("qos");
                        consumerQoS.put("price", 0.0);
                    }
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
                result.put(fCall.getFunctionName(), executeFunctionCall(fCall, ce, query, complexity));
            } else {
                // All other context functions that are not either "avg" or "cluster".
                Object execute = executeFunctionCall(fCall, ce, query, complexity);
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
        if(consumerQoS == null){
            consumerQoS = sla.getJSONObject("sla").getJSONObject("qos");
        }

        CdqlResponse cdqlResponse = CdqlResponse.newBuilder().setStatus("200")
                .setQueryId(queryId)
                .setBody(result.toString())
                .setAdmin(CdqlAdmin.newBuilder()
                        .setRtmax(consumerQoS.getJSONObject("rtmax").getLong("value"))
                        .setPrice(consumerQoS.has("price")? consumerQoS.getDouble("price") :
                                sla.getJSONObject("sla").getJSONObject("price").getDouble("value"))
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
                        JSONObject appObj = ce.get(resultEntity);
                        if(appObj.has("results")){
                            appliee = ce.get(resultEntity).getJSONArray("results");
                        }
                        else {
                            appliee = new JSONArray();
                            appliee.put(appObj);
                        }
                    }
                    else {
                        JSONObject comparator = ce.get(ca.getEntityName());
                        if(comparator.has("results"))
                            comparison = comparator.getJSONArray("results")
                                    .getJSONObject(0)
                                    .getJSONObject(ca.getAttributeName());
                        else
                            comparison = (JSONObject) comparator.get(ca.getAttributeName());

                        double lat2 = comparison.getDouble("latitude");
                        double long2 = comparison.getDouble("longitude");

                        double distanceThreshold = 0.0;
                        if(value.startsWith("{")){
                            // TODO:
                            // Should convert any unit to Km
                            JSONObject val = new JSONObject(value);
                            distanceThreshold = Double.valueOf(val.getDouble("value"));
                            switch(val.getString("unit")){
                                case "m": distanceThreshold = distanceThreshold/1000.0; break;
                                case "mi": distanceThreshold = distanceThreshold * 1.60934; break;
                            }
                        }
                        else
                            // Assuming it to be in Km
                            distanceThreshold = Double.valueOf(value);

                        for(Object resItem: appliee){
                            JSONObject item = ((JSONObject) resItem).getJSONObject(attribute);
                            if(isSatisfied(distance(item.getDouble("latitude"),
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

    private static boolean isSatisfied(double distance, String operator, double value) throws WrongOperatorException {
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

        // Radius of the earth in kilometers.
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double r = 6371;
        return(c * r);
    }

    public static Object executeFunctionCall(FunctionCall fCall, Map<String, JSONObject> ce, CDQLQuery query, double complexity) {
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
                        fCallTemp.addArguments(Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_JSON)
                                .setStringValue(executeFunctionCall(argument.getFunctioncall(), ce, query, complexity).toString()));
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
            return embedSituation(fCallTemp.build(), ce, query, complexity);
        } else {
            return executeSituationFunction(fCallTemp.build(), complexity);

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

    private static JSONObject embedSituation(FunctionCall fCall, Map<String, JSONObject> ce, CDQLQuery query, double complexity) {

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
            entity.put(functionName, executeFunctionCall(fCallTemp.build(), ce, query, complexity));
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

    public static Object executeSituationFunction(FunctionCall fCall, double complexity) {
        // Checking cache for the situation
        String situId = Utilities.combineHashKeys(fCall.getArgumentsList().stream().map(x -> {
            // This is a quick fix to defining a hashkey for the situations.
            String strValue = x.getStringValue();
            if(strValue.startsWith("{")) return (new JSONObject(strValue)).getString("hashkey");
            else return strValue;
        }).collect(Collectors.toList()));

        SituationLookUp situLookup = SituationLookUp.newBuilder().setFunction(fCall)
                .setUniquehashkey(situId).build();

        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        SQEMResponse situCacheRes = sqemStub.handleSituationInCache(situLookup);

        if(situCacheRes.getStatus().equals("200")){
            JSONArray finalResult = new JSONArray(situCacheRes.getBody());
            if (finalResult.length() == 1) {
                Object outcome = finalResult.getJSONObject(0).get("outcome");
                if(outcome instanceof JSONObject){
                    return ((JSONObject) outcome).get(fCall.getFunctionName());
                }
                return outcome;
            } else {
                JSONObject resultWrapper = new JSONObject();
                resultWrapper.put("results", finalResult);
                return resultWrapper;
            }
        }
        else {
            long zeroTime = 0;
            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub_2
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            CREServiceGrpc.CREServiceBlockingStub creStub
                    = CREServiceGrpc.newBlockingStub(CREChannel.getInstance().getChannel());

            List<Operand> arguments = fCall.getArgumentsList();

            SituationFunction function;
            if(situCacheRes.getSituation() != null)
                function = situCacheRes.getSituation();
            else {
                SituationFunctionResponse response = sqemStub_2.findSituationByTitle(
                        SituationFunctionRequest.newBuilder()
                        .setName(fCall.getFunctionName()).build());
                if(response.getStatus().equals("200")) function = response.getSFunction();
                else throw new RuntimeException("Provided situation function: " + fCall.getFunctionName() + " can not be found.");
            }

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

                            Map.Entry<String, ContextEntityType> findEntity = tempList.entrySet().stream().filter(p -> p.getValue().getType().equals(entityType)).findFirst().get();
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
                            if(entity.has("zeroTime")) {
                                if(zeroTime == 0 || entity.getLong("zeroTime") < zeroTime)
                                    zeroTime = entity.getLong("zeroTime");
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
                        if(entities.has("zeroTime")) {
                            if(zeroTime == 0 || entities.getLong("zeroTime") < zeroTime)
                                zeroTime = entities.getLong("zeroTime");
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

            // Refreshing the context
            long finalZeroTime = zeroTime;
            SituationLookUp finalSituLookup = situLookup.toBuilder().setSituation(function).build();;
            Executors.newCachedThreadPool().submit(() -> {
                JSONObject situCacheObj = new JSONObject();
                situCacheObj.put("outcome", finalResult);
                situCacheObj.put("zeroTime", finalZeroTime > 0? finalZeroTime : System.currentTimeMillis());
                refreshOrCacheContext(Integer.parseInt(situCacheRes.getStatus()), finalSituLookup,
                        situCacheObj, complexity);
            });

            if (finalResult.length() == 1) {
                Object outcome = finalResult.getJSONObject(0).get("outcome");
                if(outcome instanceof JSONObject){
                    String situationName = function.getSituations(0).getSituationName();
                    return ((JSONObject) outcome).get(situationName);
                }
                return outcome;
            } else {
                JSONObject resultWrapper = new JSONObject();
                resultWrapper.put("results", finalResult);
                return resultWrapper;
            }
        }
    }

    private static AbstractMap.SimpleEntry retrieveContext(ContextEntity targetEntity, String contextServicesText,
                                                           HashMap<String,String> params, int limit, String criticality,
                                                           JSONObject consumerSLA, double complexity, Set<String> attributes,
                                                           List<String> operators, ContextEntity subEnt) {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        // This list is currently unordered.
        JSONArray contextServices = new JSONArray(contextServicesText);
        JSONObject qos = consumerSLA.getJSONObject("sla").getJSONObject("qos");

        // This for loop returns the context data from the first context service.
        // Although, there may be multiple other context services, this loop assumes the context services are ordered.
        // Therefore, the 1st SC in the list is the best match (Input from Kanaka).
        for (Object ctSer : contextServices) {
            // Mapping context service with SLA constraints
            JSONObject conSer = mapServiceWithSLA((JSONObject) ctSer, qos, targetEntity.getType(), criticality.toLowerCase());

            // When the context provider allows for caching and the CMP does caching too.
            if(conSer.getJSONObject("sla").getBoolean("cache") && cacheEnabled){
                JSONArray candidate_keys = conSer.getJSONObject("sla").getJSONArray("key");
                String keys = "";
                for(Object k : candidate_keys)
                    keys = keys.isEmpty() ? k.toString() : keys + "," + k.toString();

                JSONObject slaObj = conSer.getJSONObject("sla");

                // By this line, I should have completed identifying the query class
                CacheLookUp.Builder lookup = CacheLookUp.newBuilder()
                        .putAllParams(params)
                        .setEt(targetEntity.getType())
                        .setServiceId(conSer.getJSONObject("_id").getString("$oid"))
                        .setCheckFresh(true)
                        .setKey(keys).setQClass("1") // TODO: Assign the correct context query class
                        .setUniformFreshness(slaObj.getJSONObject("freshness").toString()) // current lifetime & REQUESTED fthresh for the query
                        .setSamplingInterval(slaObj.getJSONObject("updateFrequency").toString()) // sampling
                        .addAllOperators(operators); // Operators of the params

                // Look up also considers known refreshing logics
                SQEMResponse data = sqemStub.handleContextRequestInCache(lookup.build());

                if(data.getStatus().equals("400") || data.getStatus().equals("404")){
                    // Need to fetch from a different Context Provider for 2 scenarios when partial misses occur when proactively refreshed, and
                    // (1) The data are streamed
                    // (2) The sensor is sampling at a certain rate

                    // IMPORTANT:
                    // Here, when at least one of the entities originating from a context provider is invalid
                    // it suggests a re-retrieval from the context provider and update all the entities at once.
                    // But this is sorted in the refresh logic since any entity refreshed before proactive refreshing
                    // are re-scheduled.
                    if(data.getStatus().equals("400") && data.getMeta().equals("proactive_shift")
                            && slaObj.getJSONObject("updateFrequency").getDouble("value") > 0)
                        continue; // This continue means alternative CP retriveal

                    // Fetching from the same context provider OR it's stream otherwise
                    if(!slaObj.getBoolean("autoFetch")){
                        // Fetching from an API
                        AbstractMap.SimpleEntry<String,List<String>> retEntity =
                                RetrievalManager.executeFetch(
                                conSer.toString(), slaObj.getJSONObject("qos"), params,
                                conSer.getJSONObject("_id").getString("$oid"),
                                consumerSLA.getJSONObject("_id").getString("$oid"),
                                targetEntity.getType().getType(), attributes,
                                cacheEnabled && data.getStatus().equals("404") && data.getHashKey().isEmpty(),
                                        subEnt) ;

                        // There is problem with the current context provider which makes it unsuitable for retrieving now.
                        // Therefore, has to move to the next context provider
                        if(retEntity == null) continue;

                        Executors.newCachedThreadPool().submit(()
                                -> refreshOrCacheContext(slaObj, Integer.parseInt(data.getStatus()),
                                lookup, retEntity.getKey(), data.getMeta(), // Meta can be Empty if it's not an entity.
                                complexity, retEntity.getValue()));
                        return new AbstractMap.SimpleEntry(retEntity.getKey(),qos.put("price",
                                consumerSLA.getJSONObject("sla").getJSONObject("price").getDouble("value")));
                    }
                    else {
                        // This  means the context retrieval occurs in to the storage.
                        // Meaning either the context provider push data via the endpoint, subscribed with the Kafka broker
                        // the periodic retrieval scheduler is executing.
                        AbstractMap.SimpleEntry<String,List<String>> status =
                                RetrievalManager.executeStreamRead(conSer.toString(),
                                conSer.getJSONObject("_id").getString("$oid"), params,
                                cacheEnabled && data.getStatus().equals("404") && data.getHashKey().isEmpty());
                        if(status.getKey().equals("200"))
                            return new AbstractMap.SimpleEntry(null, SimpleContainer.newBuilder()
                                .addAllHashKeys(status.getValue())
                                .setStatus(data.getStatus()).setContextService(conSer.toString())
                                .setRefPolicy(data.getMeta()).build());
                        continue; // Moving to the next context provider since it is currently unavailable.
                    }
                }
                else if (data.getStatus().equals("200")){
                    // Complete Hit Return
                    return new AbstractMap.SimpleEntry(data.getBody(),
                            qos.put("price", consumerSLA.getJSONObject("sla").getJSONObject("price").getDouble("value")));
                }
                else {
                    // This could be taken out.
                    log.severe("Error had occured when fetching from cache and/or storage!");
                    return null;
                }
            }

            // No caching block
            JSONObject slaObj = conSer.getJSONObject("sla");
            if(conSer.getJSONObject("sla").getBoolean("autoFetch")){
                // Fully redirector mode of operation
                AbstractMap.SimpleEntry<String,List<String>> status =
                        RetrievalManager.executeStreamRead(conSer.toString(),
                        conSer.getJSONObject("_id").getString("$oid"), params, cacheEnabled);
                if(status.getKey().equals("200"))
                    return new AbstractMap.SimpleEntry(null, SimpleContainer.newBuilder()
                            .setContextService(conSer.toString())
                            .addAllHashKeys(status.getValue()).build());
                continue; // Moving to the next context provider since it is currently unavailable.
            }
            else {
                AbstractMap.SimpleEntry retEntity = RetrievalManager.executeFetch(conSer.toString(),
                        slaObj.getJSONObject("qos"), params,
                        conSer.getJSONObject("_id").getString("$oid"),
                        consumerSLA.getJSONObject("_id").getString("$oid"),
                        targetEntity.getType().getType(), attributes, cacheEnabled, subEnt);

                if(retEntity != null)
                    return new AbstractMap.SimpleEntry(retEntity.getKey(),
                            qos.put("price", consumerSLA.getJSONObject("sla").getJSONObject("price").getDouble("value")));
                continue; // Moving to the next context provider since it is currently unavailable.
            }
        }

        log.severe("Runtime error during context retrieval.");
        return null;
    }

    private static void refreshOrCacheContext(JSONObject sla, int cacheStatus, CacheLookUp.Builder lookup,
                                              String retEntity, String currRefPolicy, double complexity,
                                              List<String> hashKeys){
        try {
            CPREEServiceGrpc.CPREEServiceFutureStub asynStub
                    = CPREEServiceGrpc.newFutureStub(CPREEChannel.getInstance().getChannel());

            if(cacheStatus == 400){
                // 400 means the cache missed due to invalidity
                // The cached context needs to be refreshed.
                JSONObject retrievedContext = new JSONObject(retEntity);
                if(!retrievedContext.has("results")){
                    asynStub.refreshContext(ContextRefreshRequest.newBuilder()
                            .setHashKey(hashKeys.get(0))
                            // Removed attributes
                            .setRefreshPolicy(currRefPolicy)
                            .setComplexity(complexity)
                            // currRefPolicy will be empty when at least one of the entities under a CS is invalid.
                            .setRequest(CacheRefreshRequest.newBuilder()
                                    .setSla(sla.toString())
                                    .setReference(lookup)
                                    .setJson(retEntity)).build());
                }
                else {
                    int numberOfIterations = (int)(hashKeys.size()/numberOfItemsPerTask) + 1;
                    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
                    JSONArray entityData = retrievedContext.getJSONArray("results");

                    for (int factor = 0; factor<numberOfIterations; factor++)
                    {
                        int finalFactor = factor;
                        executorService.submit(() -> {
                            int start = numberOfItemsPerTask * finalFactor;
                            int end =  Math.min(hashKeys.size(), start + numberOfItemsPerTask);

                            // Sending cache hit or miss record to performance monitor.
                            for(int i = start; i < end; i++){
                                asynStub.refreshContext(ContextRefreshRequest.newBuilder()
                                        .setHashKey(hashKeys.get(i))
                                        // Removed attributes
                                        .setRefreshPolicy(currRefPolicy)
                                        .setComplexity(complexity)
                                        // currRefPolicy will be empty when at least one of the entities under a CS is invalid.
                                        .setRequest(CacheRefreshRequest.newBuilder()
                                                .setSla(sla.toString())
                                                .setReference(lookup)
                                                .setJson(entityData.getJSONObject(i).toString())).build());
                            }
                        });
                    }
                    executorService.shutdown();

                    try {
                        executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
                    } catch (InterruptedException e) {
                        log.severe("Executor failed to execute with error: " + e.getMessage());
                    }
                }
            }
            else if(cacheStatus == 404 && registerState){
                // 404 means all context entities are not at all cached
                // Trigger Selective Caching Evaluation
                JSONObject retrievedContext = new JSONObject(retEntity);
                if(!retrievedContext.has("results")){
                    asynStub.evaluateAndCacheContext(CacheSelectionRequest.newBuilder()
                            .setHashKey(hashKeys.get(0))
                            .setSla(sla.toString())
                            .setContext(retEntity)
                            .setContextLevel(CacheLevels.ENTITY.toString().toLowerCase())
                            .setReference(lookup)
                            .setComplexity(complexity)
                            // Removed attributes
                            .build());
                }
                else {
                    int numberOfIterations = (int)(hashKeys.size()/numberOfItemsPerTask) + 1;
                    ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
                    JSONArray entityData = retrievedContext.getJSONArray("results");

                    for (int factor = 0; factor<numberOfIterations; factor++)
                    {
                        int finalFactor = factor;
                        executorService.submit(() -> {
                            int start = numberOfItemsPerTask * finalFactor;
                            int end =  Math.min(hashKeys.size(), start + numberOfItemsPerTask);
                            for(int i = start; i < end; i++){
                                asynStub.evaluateAndCacheContext(CacheSelectionRequest.newBuilder()
                                        .setHashKey(hashKeys.get(i))
                                        .setSla(sla.toString())
                                        .setContext(entityData.getJSONObject(i).toString())
                                        .setContextLevel(CacheLevels.ENTITY.toString().toLowerCase())
                                        .setReference(lookup)
                                        .setComplexity(complexity)
                                        // Removed attributes
                                        .build());
                            }
                        });
                    }
                }
            }
        }
        catch (Exception ex){
            log.severe("Error occured in Refresh Or Cache Context method. See message for details.");
            log.severe("Error: " + ex.getMessage());
        }
    }

    private static void refreshOrCacheContext(int cacheStatus, SituationLookUp lookup, Object inferedSituation,
                                              double complexity){
        try {
            CPREEServiceGrpc.CPREEServiceFutureStub asynStub
                    = CPREEServiceGrpc.newFutureStub(CPREEChannel.getInstance().getChannel());

            if(cacheStatus == 400){
                // 400 means the cache missed due to invalidity
                // The cached context needs to be refreshed.
                asynStub.refreshContext(ContextRefreshRequest.newBuilder()
                        .setRequest(CacheRefreshRequest.newBuilder()
                                .setSituReference(lookup)
                                .setContextLevel(CacheLevels.SITU_FUNCTION.toString().toLowerCase())
                                .setJson(inferedSituation.toString())).build());
            }
            else if(cacheStatus == 404 && registerState){
                // 404 means all context entities are not at all cached
                // Trigger Selective Caching Evaluation
                asynStub.evaluateAndCacheContext(CacheSelectionRequest.newBuilder()
                        .setSituReference(lookup)
                        .setComplexity(complexity)
                        .setContext(inferedSituation.toString())
                        .setContextLevel(CacheLevels.SITU_FUNCTION.toString().toLowerCase())
                        .build());
            }
        }
        catch (Exception ex){
            log.severe("Error occured in Refresh Or Cache Context method for situations.");
            log.severe("Error: " + ex.getMessage());
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

    public static ContextRequest.Builder generateContextRequest(ContextEntity targetEntity, CDQLQuery query,
                                                                Map<String, JSONObject> ce, int page, int limit, double complexity) {
        ArrayList<CdqlConditionToken> list = new ArrayList(targetEntity.getCondition().getRPNConditionList());

        for (int i = 0; i < list.size(); i++) {
            CdqlConditionToken item = list.get(i);

            switch (item.getType()) {
                case Function:
                    FunctionCall.Builder fCall = item.getFunctionCall().toBuilder();
                    FunctionCall.Builder fCallTemp = FunctionCall.newBuilder().setFunctionName(fCall.getFunctionName());
//                    if (fCall.getFunctionName().toLowerCase().equals("distance") || fCall.getFunctionName().toLowerCase().equals("geoinside") || fCall.getFunctionName().toLowerCase().equals("geoinsidebox")) {
//                        continue;
//                    }
                    fCallTemp.addAllSubItems(fCall.getSubItemsList());
                    int requiredArgs = fCall.getArgumentsList().size();
                    for (int j = 0; j < fCall.getArgumentsList().size(); j++) {
                        Operand argument = fCall.getArgumentsList().get(j);
                        if (argument.getType() == OperandType.CONTEXT_ENTITY) {
                            ContextEntity object = argument.getContextEntity();
                            String entityID = object.getEntityID();
                            if (!entityID.equals(targetEntity.getEntityID())) {
                                ContextEntity findEntity = query.getDefine().getDefinedEntitiesList().stream()
                                        .filter(e -> e.getEntityID().equals(entityID)).findFirst().get();
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
                                // This means when the entity in the argument is not should be in the select result.
                                JSONObject ceResult = ce.get(entityNameTemp);
                                if(ceResult.has("results")){
                                    Object location = ceResult.getJSONArray("results").getJSONObject(0).get(attributeNameTemp);
                                    String handelQuantitativeValue = handelQuantitativeValue(location.toString());
                                    fCall.setArguments(j, Operand.newBuilder()
                                            .setContextAttribute(ca)
                                            .setType(OperandType.CONTEXT_VALUE_JSON)
                                            .setStringValue(handelQuantitativeValue));
                                }
                                else{
                                    String handelQuantitativeValue = handelQuantitativeValue(ceResult.get(attributeNameTemp).toString());
                                    fCall.setArguments(j, Operand.newBuilder()
                                            .setContextAttribute(ca)
                                            .setType(OperandType.CONTEXT_VALUE_JSON)
                                            .setStringValue(handelQuantitativeValue));
                                    if(fCallTemp.getArgumentsList().size()>=j)
                                        fCallTemp.addArguments(j, Operand.newBuilder()
                                                .setContextAttribute(ca)
                                                .setType(OperandType.CONTEXT_VALUE_JSON)
                                                .setStringValue(handelQuantitativeValue));
                                }

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
                        String val = executeFunctionCall(fCallTemp.build(), ce, query, complexity).toString();
                        CdqlConstantConditionTokenType constantType = getConstantType(val);
                        CdqlConditionToken constantToken = CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Constant)
                                .setStringValue(val)
                                .setConstantTokenType(constantType).build();
                        list.set(i, constantToken);
                    }
                    else if(fCall.getFunctionName().toLowerCase().equals("distance") ||
                            fCall.getFunctionName().toLowerCase().equals("geoinside") ||
                            fCall.getFunctionName().toLowerCase().equals("geoinsidebox")) {
                        CdqlConditionToken.Builder itemBuilder = item.toBuilder();
                        itemBuilder.setFunctionCall(fCall.build());
                        list.set(i, itemBuilder.build());
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
        ContextRequest.Builder cr = ContextRequest.newBuilder().setEt(targetEntity.getType())
                .setCondition(tempContextEntity.getCondition())
                .setMeta(query.getMeta())
                .setPage(page)
                .setLimit(limit)
                .setEntityID(targetEntity.getEntityID())
                .addAllReturnAttributes(returnAttributes);

        return cr;
    }

    public static JSONObject executeSQEMQuery(ContextEntity targetEntity, CDQLQuery query,
                                                           Map<String, JSONObject> ce, int page, int limit, double complexity) {
        AbstractMap.SimpleEntry res = executeSQEMQuery(targetEntity, query, ce, page, limit, null, complexity);
        return (JSONObject) res.getKey();
    }

    private static AbstractMap.SimpleEntry executeSQEMQuery(ContextEntity targetEntity, CDQLQuery query,
                                               Map<String, JSONObject> ce, int page, int limit,
                                               String contextService, double complexity) {

        ContextRequest.Builder cr = generateContextRequest(targetEntity, query, ce, page, limit, complexity);

        if(contextService != null) {
            JSONObject cs = new JSONObject(contextService);
            cr.setProviderId(cs.getJSONObject("_id").getString("$oid"));
        }

        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        Iterator<Chunk> data = sqemStub.handleContextRequest(cr.build());

        ByteString.Output output = ByteString.newOutput();

        data.forEachRemaining(c -> {
            output.write(c.getData().toByteArray(), c.getIndex() * MAX_MESSAGE_SIZE, c.getData().size());
        });

        JSONObject invoke = new JSONObject();
        String entityCollection = "";

        try {
            SQEMResponse sqemResponse = SQEMResponse.parseFrom(output.toByteString());
            entityCollection = sqemResponse.getBody();
            if (sqemResponse.getMeta() != null) {
                invoke.put("results", new JSONArray(entityCollection));
                invoke.put("meta", new JSONObject(sqemResponse.getMeta()));
            }
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }

        if(contextService != null) {
            JSONObject slaObj = new JSONObject(contextService);
            String entType = slaObj.getJSONObject("info").getString("name");
            Double fthresh = slaObj.getJSONObject("sla").getJSONObject("freshness").getDouble("fthresh");

            SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
            asyncStub.summarizeAge(Statistic.newBuilder().setMethod(entityCollection)
                    .setStatus(entType).setEarning(fthresh).build());

            JSONObject cs = new JSONObject(contextService);
            JSONObject qos = cs.getJSONObject("sla").getJSONObject("qos");
            return new AbstractMap.SimpleEntry(invoke,
                    qos.put("price", cs.getJSONObject("sla").getJSONObject("cost").getDouble("value")));
        }

        SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
        asyncStub.summarizeAge(Statistic.newBuilder().setMethod(entityCollection)
                .setStatus(targetEntity.getType().getType())
                .setEarning(0.7).build()); // Setting the default

        return new AbstractMap.SimpleEntry(invoke, null);
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
