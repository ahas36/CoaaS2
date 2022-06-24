package au.coaas.cqc.executor;

import au.coaas.base.proto.ListOfString;

import au.coaas.cre.proto.*;

import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;

import au.coaas.grpc.client.CREChannel;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;

import au.coaas.cqc.proto.*;
import au.coaas.cqp.proto.*;
import au.coaas.sqem.proto.*;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.IOException;
import java.net.URLEncoder;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

public class PullBasedExecutor {

    // This is the cache switch
    private static boolean cacheEnabled = true;

    private static Logger log = Logger.getLogger(PullBasedExecutor.class.getName());
    private static JsonParser parser = new JsonParser();

    private static Object getValueOfJsonObject(final JSONObject obj, String path) {
        JSONObject jo = new JSONObject(obj.toString());
        String[] split = path.split("\\.");
        for (int i = 0; i < split.length - 1; i++) {
            jo = jo.getJSONObject(split[i]);
        }

        Object result = jo.get(split[split.length - 1]);
        if(result instanceof String){
            if(((String) result).startsWith("{"))
                return new JSONObject((String) result);
            else if(((String) result).startsWith("["))
                return new JSONArray((String) result);
        }

        return result;
    }

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

    // This method executes the query plan for pull based queries
    public static CdqlResponse executePullBaseQuery(CDQLQuery query, String authToken, int page, int limit, String queryId, String criticality) {

        // Initialize values
        // How are the values assigned to 'ce'? I only see values assigned in the catch block.
        Map<String, JSONObject> ce = new HashMap<>();
        JSONObject consumerQoS = null;

        // Iterates over execution plan
        // The first loop goes over dependent sets of entities
        Collection<ListOfString> aList = query.getExecutionPlanMap().values();

        /*
        ArrayList<ListOfString> bList = new ArrayList<>();
        ListOfString lastElement = null;
        for (ListOfString element : aList) {
            lastElement = element;
        }
        bList.add(lastElement);

        ce.put("consumerCars", new JSONObject("{\"time_stamp\":\"2022-06-0318:29:31\"," +
                "\"vin\":\"C335DBB3B31248F986B99CBBBAE1E064\",\"plateNumber\":\"UMB-BHAO\"," +
                "\"specifications\":{\"make\":\"alfa-romero\",\"type\":\"convertible\"}," +
                "\"height\":{\"value\":1.67,\"unitText\":\"m\"},\"width\":{\"value\":1.63,\"unitText\":\"m\"}," +
                "\"length\":{\"value\":4.45,\"unitText\":\"m\"},\"unit\":{\"value\":\"m\",\"unitText\":\"m\"}," +
                "\"wheelBase\":{\"value\":88.6,\"unitText\":\"m\"},\"performance\":{\"value\":49.39515," +
                "\"unitText\":\"litersperkilometer\"},\"location\":{\"latitude\":-37.80411643534407," +
                "\"longitude\":144.9783143972314},\"speed\":{\"value\":60,\"unitText\":\"kmph\"}}"));
        */

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
                        String attributeName = token.getStringValue();
                        if (attributeName.equals("and") || attributeName.equals("or")) {
                            continue;
                        }
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
                                    get = getValueOfJsonObject(ce.get(valueEntityID), valueEntityBody);
                                } catch (Exception ex) {
                                    get = getValueOfJsonObject(ce.get(valueEntityID).getJSONArray("results").getJSONObject(0), valueEntityBody);
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
                        String op = rpnCondition.poll().getStringValue();

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
                // The context query executes as a collection of context-requests. That is why service discovery and executing happen per entity over a loop.
                // Not the loop here isn't parallelized either. Now consider, multiple parallel context queries, trying to access the same context. Discovering and executing is not efficient at all.

                String contextService = ContextServiceDiscovery.discover(entity.getType(), terms.keySet());

                if (contextService != null) {
                    ContextRequest contextRequest = generateContextRequest(entity, query, ce, 0, 0);
                    List<CdqlConditionToken> rpnConditionList = contextRequest.getCondition().getRPNConditionList();

                    String key = null;
                    String value = null;
                    HashMap<String,String> params = new HashMap();
                    for (CdqlConditionToken cdqlConditionToken : rpnConditionList) {
                        switch (cdqlConditionToken.getType()) {
                            case Constant:
                                value = cdqlConditionToken.getStringValue();
                                if (key != null) {
                                    params.put(key,value);
                                    key = null;
                                    value = null;
                                }
                                break;
                            case Attribute:
                                key = cdqlConditionToken.getContextAttribute().getAttributeName();
                                if (value != null) {
                                    params.put(key,value);
                                    key = null;
                                    value = null;
                                }
                                break;
                            default:
                        }
                    }

                    AbstractMap.SimpleEntry cacheResult = retrieveContext(entity, authToken, contextService, params, criticality);
                    if(cacheResult != null){
                        ce.put(entity.getEntityID(), new JSONObject((String) cacheResult.getKey()));
                        if(consumerQoS == null)  consumerQoS = (JSONObject) cacheResult.getValue();
                    }

                }
                else {
                    // Querying from registered entities. This does not guarantee "completeness" of context.
                    // Registered entities updated. But not the most fresh. So, best when the entities are static, e.g., buildings.
                    // In fact, querying from MongoDB is actually to query the "Context Service Descriptions".

                    // This can also be if the context service is a data stream.

                    AbstractMap.SimpleEntry rex = executeSQEMQuery(entity, query, ce, page, limit, authToken);
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
        for (Map.Entry<String, ListOfContextAttribute> entry : query.getSelect().getSelectAttrsMap().entrySet()) {
            String entity = entry.getKey();
            result.put(entity, ce.get(entity));
        }

        for (FunctionCall fCall : query.getSelect().getSelectFunctionsList()) {
            if (fCall.getFunctionName().equals("avg") || fCall.getFunctionName().equals("cluster")) {
                result.put(fCall.getFunctionName(), executeFunctionCall(fCall, ce, query));
            } else {
                // All other context functions that are not either "avg" or "cluster".
                Object execute = executeFunctionCall(fCall, ce, query);

                result.put(fCall.getFunctionName(), execute);
//                if (execute.trim().startsWith("[")) {
//                    result.put(fCall.getFunctionName(), new JSONArray(execute.replaceAll("\"", "")));
//                } else {
//                    if (fCall.getSubItemsList().isEmpty()) {
//                        result.put(fCall.getFunctionName(), execute);
//                    } else {
//                        result.put(fCall.getSubItemsList().get(0), execute);
//                    }
//                }
            }
        }

        //String queryOuptput = OutputHandler.handle(result, query.getConfig().getOutputConfig());
        // If this response need to be cached, this is where the caching action should happen.
        CdqlResponse cdqlResponse = CdqlResponse.newBuilder().setStatus("200")
                .setBody(result.toString()).setQueryId(queryId)
                .setAdmin(CdqlAdmin.newBuilder()
                        .setRtmax(consumerQoS.getJSONObject("rtmax").getLong("value"))
                        .setPrice(consumerQoS.getDouble("price"))
                        .setRtpenalty(consumerQoS.getJSONObject("rtmax").getJSONObject("penalty")
                                .getDouble("value"))
                        .build())
                .build();

        return cdqlResponse;
    }

    private static Object executeFunctionCall(FunctionCall fCall, Map<String, JSONObject> ce, CDQLQuery query) {
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

    private static Object executeSituationFunction(SituationFunction function, FunctionCall fCall) {

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
                    Map.Entry<String, ContextEntityType> findEntity = tempList.entrySet().stream().filter(p -> p.getValue().toString().equals(entityType)).findFirst().get();
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
            return finalResult.getJSONObject(0).get("outcome");
        } else {
            JSONObject resultWrapper = new JSONObject();
            resultWrapper.put("results", finalResult);
            return resultWrapper;
        }
    }

    private static AbstractMap.SimpleEntry retrieveContext(ContextEntity targetEntity, String authToken, String contextServicesText,
                                                            HashMap<String,String> params, String criticality) {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        JSONArray contextServices = new JSONArray(contextServicesText);

        // Fetching Consumer SLA
        JSONObject sla = null;
        SQEMResponse slaMessage = sqemStub.getConsumerSLA(AuthToken.newBuilder().setToken(authToken).build());
        if(slaMessage.getStatus().equals("200"))
            sla = new JSONObject(slaMessage.getBody());

        for (int i = 0; i < contextServices.length(); i++) {
            // Mapping context service with SLA constraints
            JSONObject qos = sla.getJSONObject("sla").getJSONObject("qos");
            JSONObject conSer = contextServices.getJSONObject(i);

            if(qos.getBoolean("staged")){
                conSer = mapServiceWithSLA(conSer, qos, targetEntity.getType(), criticality.toLowerCase());
            }

            if(conSer.getJSONObject("sla").getBoolean("cache") && cacheEnabled){
                JSONArray candidate_keys = conSer.getJSONObject("sla").getJSONArray("key");
                String keys = "";
                for(Object k : candidate_keys)
                    keys = keys.isEmpty() ? keys : keys + "," + k.toString();

                final CacheLookUp lookup = CacheLookUp.newBuilder().putAllParams(params)
                        .setEt(targetEntity.getType())
                        .setServiceId(conSer.getJSONObject("_id").toString())
                        .setCheckFresh(true)
                        .setKey(keys)
                        .setUniformFreshness(conSer.getJSONObject("sla")
                                .getJSONObject("freshness").toString())
                        .build();

                SQEMResponse data = sqemStub.handleContextRequestInCache(lookup);

                if(data.getBody().equals("") && data.getStatus() != "500"){
                    String retEntity = executeFetch(conSer.toString(), params);

                    switch(data.getStatus()){
                        case "400": {
                            SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                            asyncStub.refreshContextEntity(CacheRefreshRequest.newBuilder()
                                    .setReference(lookup)
                                    .setJson(retEntity).build());
                            break;
                        }
                        case "404": {
                            // Trigger Selective Caching Evaluation (Implement to run in a separate thread)
                            // In Phase 1, "Cache All policy is used if allowed in the SLA"
                            SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                                    = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());

                            asyncStub.cacheEntity(CacheRequest.newBuilder()
                                    .setJson(retEntity)
                                    // This cache life should be saved in the eviction registry
                                    .setCachelife(600)
                                    .setReference(lookup).build());
                            break;
                        }
                    }

                    return new AbstractMap.SimpleEntry(retEntity,qos.put("price",
                            sla.getJSONObject("sla").getJSONObject("price").getDouble("value")));
                }
                else {
                    return new AbstractMap.SimpleEntry(data.getBody(),
                            qos.put("price", sla.getJSONObject("sla").getJSONObject("price").getDouble("value")));
                }
            }

            String retEntity = executeFetch(conSer.toString(), params);
            if(retEntity != null)
                return new AbstractMap.SimpleEntry(retEntity,
                        qos.put("price", sla.getJSONObject("sla").getJSONObject("price").getDouble("value")));

        }

        return null;
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

    private static String executeFetch(String contextService, HashMap<String,String> params) {
        long startTime = System.currentTimeMillis();

        CSIServiceGrpc.CSIServiceBlockingStub csiStub
                = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());

        final ContextServiceInvokerRequest.Builder fetchRequest = ContextServiceInvokerRequest.newBuilder();
        fetchRequest.putAllParams(params);

        final ContextService cs = ContextService.newBuilder().setJson(contextService).build();
        fetchRequest.setContextService(cs);
        CSIResponse fetch = csiStub.fetch(fetchRequest.build());

        long endTime = System.currentTimeMillis();
        SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
        asyncStub.logPerformanceData(Statistic.newBuilder()
                .setMethod("executeFetch").setStatus(fetch.getStatus())
                .setTime(endTime-startTime).setCs(fetchRequest).setEarning(0)
                .setCost(fetch.getStatus().equals("200")? fetch.getSummary().getPrice() : 0).build());
        // Here, the response to fetch is not 200, there is not monetary cost, but there is an abstract cost of network latency

        if (fetch.getStatus().equals("200")) {
            return fetch.getBody();
        }

        return null;
    }

    private static ContextRequest generateContextRequest(ContextEntity targetEntity, CDQLQuery query, Map<String, JSONObject> ce, int page, int limit) {
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

    private static AbstractMap.SimpleEntry executeSQEMQuery(ContextEntity targetEntity, CDQLQuery query,
                                               Map<String, JSONObject> ce, int page, int limit, String authToken) {
        JSONObject sla = null;

        ContextRequest cr = generateContextRequest(targetEntity, query, ce, page, limit);

        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse slaMessage = sqemStub.getConsumerSLA(AuthToken.newBuilder().setToken(authToken).build());
        String status = slaMessage.getStatus();
        if(status.equals("200"))
            sla = new JSONObject(slaMessage.getBody());
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


        //ToDo validate return entities
//        JSONArray result = new JSONArray();
//        JSONArray jsonArray = invoke.getJSONArray("result");
//
//        for (int i = 0; i < Math.min(jsonArray.length(), 10); i++) {
//            JSONObject carpark = jsonArray.getJSONObject(i);
//            if (this.evaluate(carpark, ce, targetEntity.getCondition().getRPNCondition())) {
//                try {
//                    carparks.put(JsonSimplifier.Simplify(carpark.getJSONObject("document")));
//                } catch (Exception e) {
//                    carparks.put(JsonSimplifier.Simplify(carpark));
//                }
//            }
//        }

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
        String unitCode = jsonValue.getString("unitCode");
        String defaultUnitCode = getDefaultUnitCode(unitCode);

        return unitConverter(unitCode, defaultUnitCode, Double.valueOf(jsonValue.getString("value"))).toString();
    }

    private static Double unitConverter(String originUnit, String targetUnit, double value) {
        switch (originUnit.toLowerCase()) {
            case "mm":
                switch (targetUnit.toLowerCase()) {
                    case "mm":
                        return value;
                    case "km":
                        return value / 1000000;
                    case "cm":
                        return value / 10;
                    case "m":
                        return value / 1000;
                    default:
                        return null;
                }
            case "km":
            case "cm":
            case "m":
            default:
                return null;
        }
    }

    private static String getDefaultUnitCode(String unitCode) {
        switch (unitCode.toLowerCase()) {
            case "mm":
            case "km":
            case "cm":
            case "m":
                return "m";
        }
        return null;
    }

}
