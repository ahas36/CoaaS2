package au.coaas.cqc.executor;

import au.coaas.base.proto.ListOfString;
import au.coaas.cre.proto.*;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;
import au.coaas.grpc.client.CQPChannel;
import au.coaas.grpc.client.CREChannel;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.cqc.proto.*;
import au.coaas.cqp.proto.*;
import au.coaas.sqem.proto.ContextRequest;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.SituationFunctionRequest;
import com.google.gson.Gson;
import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PullBasedExecutor {

    private static Logger log = Logger.getLogger(PullBasedExecutor.class.getName());

    private static Object getValueOfJsonObject(final JSONObject obj, String path) {
        JSONObject jo = new JSONObject(obj.toString());
        String[] split = path.split("\\.");
        for (int i = 0; i < split.length - 1; i++) {
            jo = jo.getJSONObject(split[i]);
        }
        return jo.get(split[split.length - 1]);
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

    public static CdqlResponse executePullBaseQuery(CDQLQuery query) {

        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        //initialize values
        Map<String, JSONObject> ce = new HashMap<>();
        List<ContextEntity> sqemEntities = new ArrayList<>();


        //iterates over execution plan and fetch results
        for (ListOfString entityList : query.getExecutionPlanMap().values()) {
            for (String entityID : entityList.getValueList()) {
                ContextEntity entity = query.getDefine().getDefinedEntitiesList().stream().filter(v -> v.getEntityID().equals(entityID)).findFirst().get();

                Queue<CdqlConditionToken> rpnCondition = new LinkedList<>(entity.getCondition().getRPNConditionList());

                int i = 0;
                Map<String, String> terms = new HashMap<>();
                boolean errorDetected = false;

                while (rpnCondition.peek() != null) {
                    try {
                        CdqlConditionToken token = rpnCondition.poll();
                        String attributeName = token.getStringValue();
                        if (attributeName.equals("and") || attributeName.equals("or")) {
                            continue;
                        }
                        String key = attributeName.replace(entity.getEntityID() + ".", "");
                        CdqlConditionToken valueToken = rpnCondition.poll();
                        String value = valueToken.getStringValue();
                        if (value.contains("\"")) {
                            value = value.replaceAll("\"", "");
                            String pathEncode = URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8.toString());
                            terms.put(key, pathEncode);
                            // serviceURI = serviceURI.replace("{" + key + "}", pathEncode);
                        } else if (value.contains("{")) {
                        } else if (valueToken.getType() != CdqlConditionTokenType.Constant && (ce.containsKey(value) || value.contains("."))) {
                            String valueEntityID = value.split("\\.")[0];
                            String valueEntityBody = value.replace(valueEntityID + ".", "");
                            Object get = null;
                            if (valueEntityID.equals(valueEntityBody)) {
                                get = ce.get(valueEntityID);
                            } else {
                                try {
                                    get = getValueOfJsonObject(ce.get(valueEntityID), valueEntityBody);

                                } catch (Exception ex) {
                                    get = getValueOfJsonObject(ce.get(valueEntityID).getJSONArray("results").getJSONObject(0), valueEntityBody);
                                }
                            }
                            if (get instanceof JSONObject) {
                                JSONObject tmp = (JSONObject) get;
                                for (String jsonKey : tmp.keySet()) {
                                    Object subItemValue = tmp.get(jsonKey);
                                    if (!(subItemValue instanceof JSONObject)) {
                                        String pathEncode = URLEncoder.encode(subItemValue.toString(), java.nio.charset.StandardCharsets.UTF_8.toString());
                                        terms.put(key + "." + jsonKey, pathEncode);
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
                String contextService = ContextServiceDiscovery.discover(entity.getType(), terms.keySet());
                if (contextService == null) {
                    ce.put(entity.getEntityID(), executeSQEMQuery(entity, query, ce));
                    continue;
                }
                ce.put(entity.getEntityID(), executeFetch(entity, query, ce, contextService));
                //ToDo function call and discovery
            }
        }


        JSONObject result = new JSONObject();

        for (Map.Entry<String, ListOfContextAttribute> entry : query.getSelect().getSelectAttrsMap().entrySet()) {
            String key = entry.getKey();
            result.put(key, ce.get(key));
        }

        for (FunctionCall fCall : query.getSelect().getSelectFunctionsList()) {
            if (fCall.getFunctionName().equals("avg") || fCall.getFunctionName().equals("cluster")) {
                result.put(fCall.getFunctionName(), executeFunctionCall(fCall, ce, query));
            } else {
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

        CdqlResponse cdqlResponse = CdqlResponse.newBuilder().setStatus("200").setBody(result.toString()).build();


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
            result.put("entityType", jo.get("entityType"));
            finalResult.put(result);
        }
        JSONObject objectResult = new JSONObject();
        objectResult.put("entityType",jo.get("entityType"));
        objectResult.put("results", finalResult);
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

    private static JSONObject executeSituationFunction(SituationFunction function, FunctionCall fCall) {

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
        JSONObject resultWrapper = new JSONObject();
        resultWrapper.put("results", finalResult);
        return resultWrapper;
    }

    private static JSONObject executeFetch(ContextEntity targetEntity, CDQLQuery query, Map<String, JSONObject> ce, String contextServicesText) {
        CSIServiceGrpc.CSIServiceBlockingStub csiStub
                = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());

        final ContextServiceInvokerRequest.Builder fetchRequest = ContextServiceInvokerRequest.newBuilder();
        JSONArray contextServices = new JSONArray(contextServicesText);

        ContextRequest contextRequest = generateContextRequest(targetEntity, query, ce);

        List<CdqlConditionToken> rpnConditionList = contextRequest.getCondition().getRPNConditionList();


        String key = null;
        String value = null;
        for (CdqlConditionToken cdqlConditionToken : rpnConditionList) {
            switch (cdqlConditionToken.getType()) {
                case Constant:
                    value = cdqlConditionToken.getStringValue();
                    if (key != null) {
                        fetchRequest.putParams(key, value);
                        key = null;
                        value = null;
                    }
                    break;
                case Attribute:
                    key = cdqlConditionToken.getContextAttribute().getAttributeName();
                    if (value != null) {
                        fetchRequest.putParams(key, value);
                        key = null;
                        value = null;
                    }
                    break;
                default:
            }
        }

        for (int i = 0; i < contextServices.length(); i++) {
            final ContextService cs = ContextService.newBuilder().setJson(contextServices.getJSONObject(i).toString()).build();
            fetchRequest.setContextService(cs);
            CSIResponse fetch = csiStub.fetch(fetchRequest.build());
            if (fetch.getStatus().equals("200")) {
                return new JSONObject(fetch.getBody());
            }
        }
        return null;
    }


    private static ContextRequest generateContextRequest(ContextEntity targetEntity, CDQLQuery query, Map<String, JSONObject> ce) {
        ArrayList<CdqlConditionToken> list = new ArrayList(targetEntity.getCondition().getRPNConditionList());

        for (int i = 0; i < list.size(); i++) {
            CdqlConditionToken item = list.get(i);

            switch (item.getType()) {
                case Function:
                    FunctionCall.Builder fCall = item.getFunctionCall().toBuilder();
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
                                fCall.addArguments(j, Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_JSON).setStringValue(handelQuantitativeValue));
                                requiredArgs--;
                            }

                        } else if (argument.getType() == OperandType.CONTEXT_ATTRIBUTE) {
                            ContextAttribute ca = argument.getContextAttribute();
                            String entityNameTemp = ca.getEntityName();
                            String attributeNameTemp = ca.getAttributeName();
                            if (!entityNameTemp.equals(targetEntity.getEntityID())) {
                                String handelQuantitativeValue = handelQuantitativeValue(ce.get(entityNameTemp).get(attributeNameTemp).toString());
                                fCall.addArguments(j, Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_JSON).setStringValue(handelQuantitativeValue));
                                requiredArgs--;
                            }
                        }
                    }
                    if (requiredArgs == 0) {
                        // ToDo execute function call
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
                .addAllReturnAttributes(returnAttributes).build();

        return cr;
    }


    private static JSONObject executeSQEMQuery(ContextEntity targetEntity, CDQLQuery query, Map<String, JSONObject> ce) {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        ContextRequest cr = generateContextRequest(targetEntity, query, ce);

        JSONObject invoke = new JSONObject().put("results", new JSONArray(sqemStub.handleContextRequest(cr).getBody()));

        //ToDo validate return entities
//        JSONArray result = new JSONArray();
//        JSONArray jsonArray = invoke.getJSONArray("result");
//
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
        return invoke;
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
