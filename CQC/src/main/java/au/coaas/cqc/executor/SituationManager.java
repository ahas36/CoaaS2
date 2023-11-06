package au.coaas.cqc.executor;

import au.coaas.base.proto.ListOfString;
import au.coaas.cpree.proto.CPREEServiceGrpc;

import au.coaas.cqc.proto.*;
import au.coaas.cqc.proto.Empty;
import au.coaas.cqc.utils.GeoIndexer;
import au.coaas.cqc.utils.ReturnType;
import au.coaas.cqc.utils.Utilities;
import au.coaas.cqc.utils.enums.CacheLevels;
import au.coaas.cqc.utils.enums.HttpRequests;
import au.coaas.cqc.utils.enums.RequestDataType;
import au.coaas.cqc.utils.exceptions.WrongOperatorException;

import au.coaas.cqp.proto.*;

import au.coaas.cre.proto.CRESituation;
import au.coaas.cre.proto.ContextEvent;
import au.coaas.cre.proto.CREServiceGrpc;
import au.coaas.cre.proto.SituationRequest;

import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.grpc.client.*;

import au.coaas.sqem.proto.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.google.gson.Gson;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.util.JsonFormat;
import org.joda.time.Period;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;

import java.util.*;
import java.text.SimpleDateFormat;

import java.util.concurrent.Executors;
import java.util.stream.Stream;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class SituationManager {
    private static final boolean cacheEnabled = true;

    public static long totalExecutionTime = 0;
    public static long totalNumberOfEvents = 0;

    private static final Logger log = Logger.getLogger(SituationManager.class.getName());
    private static final String[] locAttrs = new String[]{"location", "geo" };

    public static CdqlResponse handleEvent(EventRequest eventRequest) throws Exception {
        long eventStartTime = System.currentTimeMillis();
        CREServiceGrpc.CREServiceBlockingStub stub
                = CREServiceGrpc.newBlockingStub(CREChannel.getInstance().getChannel());
        CRESituation res = stub.createSituation(SituationRequest.newBuilder()
                .setEvent(eventRequest.getEvent())
                .setProvider(eventRequest.getProvider()).build());

        // Could not find any subscriptions by ID or there exist no subscription ID attached in the event.
        // This happens when a Context provider push data. This is why in line 85, it's data is mapped to an entity.
        // Subscription IDs exist for function executions.
        if(!res.getStatus().equals("200")){
            JSONObject persEvent = new JSONObject(eventRequest.getEvent());
            ObjectMapper mapper = new ObjectMapper();
            ContextEvent.Builder eventBuilder = ContextEvent.newBuilder()
                    .setKey(persEvent.getString("key"))
                    .setProviderID(persEvent.has("providerID") ?
                            persEvent.getString("providerID") : eventRequest.getProvider())
                    .setSubscriptionID(persEvent.getString("subscriptionID"))
                    // Following are commented because they should be null.
                    // .setSubscriptionValue(persEvent.getString("subscriptionValue"))
                    .setTimestamp(persEvent.getString("timestamp"))
                    .setContextEntity(mapper.readValue(persEvent.getJSONObject("contextEntity").toString(),
                            ContextEntity.class));

            if(persEvent.has("attributes"))
                eventBuilder.setAttributes(persEvent.getJSONObject("attributes").toString());

            ContextEvent event = eventBuilder.build();

            List<SubscribedQuery> subQueries = getSubscribedQueries(event);

            JSONObject result = new JSONObject();
            Queue<JSONObject> pushList = new LinkedList<>();
            String ent_id = persEvent.getString("key");

            subQueries.stream().parallel().forEach(subscription -> {
                try {
                    boolean notRelated = true;
                    Map<String, JSONObject> temp_ce = new HashMap<>();
                    SQEMServiceGrpc.SQEMServiceBlockingStub sqemstub
                            = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
                    SQEMResponse consumer = sqemstub.getConsumerSLA(AuthToken.newBuilder().setUsername(subscription.getToken()).build());
                    JSONObject conId = new JSONObject(consumer.getBody());

                    for (ContextEntity subEntity : subscription.getRelatedEntitiesList()) {
                        // Checking if the entity for which we got the data for, is the first in the execution order
                        // to resolve whether any other entity data need to be retrieved.
                        for (ListOfString keys : subscription.getQuery().getExecutionPlanMap().values()) {
                            if((keys.getValueCount() == 1 && keys.getValueList().contains(ent_id)) || temp_ce.containsKey(ent_id)) break;

                            for(String entKey : keys.getValueList()){
                                if(entKey.equals(ent_id)) continue;
                                // Need to retrieve from the context storage.
                                Optional<ContextEntity> entity = subscription.getRelatedEntitiesList().stream()
                                        .filter(x -> x.getEntityID().equals(entKey)).findFirst();
                                // Pre-process for resolving the context service.
                                AbstractMap.SimpleEntry<Boolean, Map<String, String>> preResponse = PullBasedExecutor.processForContextService(
                                        new LinkedList<>(entity.get().getCondition().getRPNConditionList()), entity.get(), temp_ce);
                                if (preResponse.getKey()) break;
                                // Resolving context service to retrieval.
                                JSONArray contextServices = new JSONArray(
                                        ContextServiceDiscovery.discover(entity.get().getType(), preResponse.getValue().keySet()));
                                // Finally, retrieve from the Cache or Context Storage.
                                ReturnType retTy = PullBasedExecutor.getContextRequest(entity.get(),
                                        subscription.getQuery(), temp_ce, subscription.getComplexity(),
                                        conId.getJSONObject("_id").getString("$oid"));

                                for(Object con_ser : contextServices) {
                                    JSONObject conSer = (JSONObject)con_ser;
                                    conSer = PullBasedExecutor.mapServiceWithSLA(conSer, conId.getJSONObject("sla").getJSONObject("qos"),
                                            entity.get().getType(), subscription.getCriticality().toLowerCase());
                                    ContextRequest.Builder cr_build = retTy.getContextRequest().setProviderId(((JSONObject) con_ser)
                                            .getJSONObject("_id").getString("$oid"));

                                    SQEMResponse data = null;
                                    CacheLookUp.Builder lookup = null;
                                    JSONObject slaObj = conSer.getJSONObject("sla");
                                    AbstractMap.SimpleEntry<String,List<String>> status = null;

                                    // Checking cache as well if enabled.
                                    if(cacheEnabled && slaObj.getBoolean("cache")) {
                                        JSONArray candidate_keys = slaObj.getJSONArray("key");
                                        String ent_keys = "";
                                        for(Object k : candidate_keys)
                                            ent_keys = ent_keys.isEmpty() ? k.toString() : ent_keys + "," + k.toString();

                                        // By this line, I should have completed identifying the query class
                                        lookup = CacheLookUp.newBuilder()
                                                .setCheckFresh(true)
                                                .setEt(entity.get().getType())
                                                .setKey(ent_keys).setQClass("1") // TODO: Assign the correct context query class
                                                .putAllParams(retTy.getParams())
                                                .addAllOperators(retTy.getOperators()) // Operators of the params
                                                .setServiceId(conSer.getJSONObject("_id").getString("$oid"))
                                                .setUniformFreshness(slaObj.getJSONObject("freshness").toString()) // current lifetime & REQUESTED fthresh for the query
                                                .setSamplingInterval(slaObj.getJSONObject("updateFrequency").toString()); // sampling
                                        // Look up also considers known refreshing logics
                                        data = sqemstub.handleContextRequestInCache(lookup.build());

                                        if(data.getStatus().equals("400") || data.getStatus().equals("404")){
                                            // In the following, we are only considering context providers pushing data mode.
                                            status = RetrievalManager.executeStreamRead(conSer.toString(),
                                                            conSer.getJSONObject("_id").getString("$oid"), retTy.getParams(), null,
                                                            1, cacheEnabled && data.getStatus().equals("404") && data.getHashKey().isEmpty());
                                            if(!status.getKey().equals("200"))
                                            continue; // Moving to the next context provider since it is currently unavailable.
                                        }
                                        else if (data.getStatus().equals("200")){
                                            temp_ce.put(entKey, new JSONObject(data.getBody()));
                                            break;
                                        }
                                    }

                                    AbstractMap.SimpleEntry rex = PullBasedExecutor.executeSQEMQuery(cr_build.build(),
                                            conSer.toString());
                                    temp_ce.put(entKey, (JSONObject) rex.getKey());

                                    // Refreshing any partially missed context entities and evaluating to cache others.
                                    // Leaving missed ones to be cached for proactive caching.
                                    if(cacheEnabled){
                                        JSONObject finalConSer = conSer;
                                        CacheLookUp.Builder finalLookup = lookup;
                                        SQEMResponse finalData = data;
                                        AbstractMap.SimpleEntry<String, List<String>> finalStatus = status;

                                        Executors.newCachedThreadPool().submit(() -> {
                                            PullBasedExecutor.refreshOrCacheContext(
                                                    finalConSer.getJSONObject("sla"),
                                                    Integer.parseInt(finalData.getStatus()),
                                                    finalLookup, rex.getKey().toString(),
                                                    finalData.getMeta(), subscription.getComplexity(),
                                                    finalStatus.getValue());
                                        });
                                    }
                                    break;
                                }
                            }
                        }

                        if (event.getContextEntity().getType().getType().equals(subEntity.getType().getType())) {
                            JSONObject tempEventJson = persEvent.getJSONObject("attributes");
                            tempEventJson.put(event.getKey(), event.getContextEntity().getEntityID());

                            temp_ce.put(subEntity.getEntityID(), tempEventJson);
                            if (evaluateNonDeterministic(temp_ce,
                                    new LinkedList<>(subEntity.getCondition().getRPNConditionList())) == true) {
                                notRelated = false;
                                break;
                            }
                        }
                    }

                    if (notRelated) return;

                    CdqlResponse pullResponse = PullBasedExecutor.executePullBaseQuery(subscription.getQuery(),
                            subscription.getToken(), -1, -1, subscription.getQueryId(),
                            subscription.getCriticality(), subscription.getComplexity(), null, null, temp_ce);

                    String body = pullResponse.getBody();
                    JSONObject oldValue = new JSONObject(body);
                    JSONObject jsonObject = new JSONObject(body);
                    // This is the entity that the consumer selects.
//                    Map<String, ListOfContextAttribute> map = subscription.getQuery().getSelect().getSelectAttrsMap();
//                    String entityID = map.keySet().iterator().next();

//                    try {
//                        JSONObject ent = jsonObject.getJSONObject(entityID);
//                        if (ent.has("results")) {
//                            ent = ent.getJSONArray("results").getJSONObject(0);
//                        }
//                        if (!ent.get(event.getKey()).toString().equals(event.getContextEntity().getEntityID())) {
//                            return;
//                        }
//                    } catch (Exception e) {
//                        return;
//                    }

//                    for (String key : jsonObject.keySet()) {
//                        try {
//                            jsonObject.put(key, jsonObject.getJSONObject(key)
//                                    .getJSONArray("results").getJSONObject(0));
//                        } catch (Exception e) {
//                            log.info(e.getMessage() + " in handle event.");
//                        }
//                    }

//                    for (Map.Entry<String, String> entry : event.getContextEntity().getContextAttributesMap().entrySet()) {
//                        String[] keys = entry.getKey().split(":");
//                        String key = keys[keys.length - 1];
//                        Object value = persEvent.getJSONObject("attributes").get(key);
//                        jsonObject.getJSONObject(entityID).put(key, value);
//                    }

                    // Checking if the situational conditions are met to push to the consumer.
                    Queue<CdqlConditionToken> tempQueue = new LinkedList<>(subscription.getSituationList());
                    boolean flag = false;
                    try {
                        flag = evaluate(subscription.getId(), jsonObject, oldValue, tempQueue,
                                subscription.getQuery(), subscription.getComplexity());
                        if(flag) {
                            // Recording the context pushing event.
                            // Following a quick hack to present at PerCom 2024. Should rather find the primary consumer and
                            // resolve what it's key and then retrieve from temp_ce accordingly.
                            recordContextPush(jsonObject, conId.getJSONObject("_id").getString("$oid"),
                                    temp_ce.get("bike").getString("vin"));
                        }
                    } catch (Exception ex) {
                        log.severe("Error occured when evaluating the situations: " + ex.getMessage());
                        log.severe(String.valueOf(ex.getStackTrace()));
                    }

                    if (flag) {
                        pushList.add(jsonObject);
                        try {
                            PushBasedExecutor.pushContext(subscription.getCallback(), jsonObject, subscription.getId());
                        } catch (IOException ex) {
                            log.severe(ex.getMessage());
                        }
                    }
                } catch (Exception e) {
                    log.severe(e.getMessage());
                }
            });

            totalExecutionTime += System.currentTimeMillis() - eventStartTime;

            new Thread(() -> {
                SQEMServiceGrpc.SQEMServiceBlockingStub sqemstub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
                // Might need to check if the entity update happens correctly.
                sqemstub.updateEventRegistry(event);
            }).start();

            result.put("results", pushList);

            CdqlResponse cdqlResponse = CdqlResponse.newBuilder().setStatus("200")
                    .setBody(result.toString()).build();
            return cdqlResponse;
        }

        CdqlResponse cdqlResponse = CdqlResponse.newBuilder().setStatus("200").build();
        return cdqlResponse;
    }

    static ObjectMapper mapper = new ObjectMapper();

    private static List<SubscribedQuery> getSubscribedQueries(ContextEvent event) throws IOException {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        CdqlSubscription subs_res;
        if(event.getSubscriptionID() != null)
            subs_res = sqemStub.getRelatedSubscriptions(Situation.newBuilder()
                    .setSubscriptionID(event.getSubscriptionID())
                    .setTimestamp(event.getTimestamp()).build());
        else {
            subs_res = sqemStub.getRelatedSubscriptions(Situation.newBuilder()
                    .setContextEntity(event.getContextEntity())
                    .setProviderID(event.getProviderID())
                    // Following are commented because they should be null.
                    // .setSubscriptionID(event.getSubscriptionID())
                    // .setSubscriptionValue(event.getSubscriptionValue())
                    .setTimestamp(event.getTimestamp()).build());
        }

        if(subs_res.getStatus().equals("200")){
            JSONArray subs = new JSONArray(subs_res.getBody());
            for (int i = 0; i < subs.length(); i++) {
                JSONObject subitem = subs.getJSONObject(i);
                subitem.put("id", subitem.getJSONObject("_id").getString("$oid"));
                subitem.remove("_id");
            }

            return mapper.readValue(subs.toString(), new TypeReference<List<SubscribedQuery>>(){});
        }

        removeSubscriptions(event.getKey(), event.getContextEntity(), event.getProviderID());
        log.info("No relevant subscriptions found");
        return null;
    }

    private static void removeSubscriptions (String contextId, ContextEntity entity, String providerID) {
        CSIServiceGrpc.CSIServiceBlockingStub csiStub
                = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());
        csiStub.modifyCPMonitor(au.coaas.csi.proto.CPMonitor.newBuilder()
                .setContextEntity(entity).setContextID(contextId)
                .setProviderID(providerID).setDelete(true).build());

        CPREEServiceGrpc.CPREEServiceBlockingStub cpreeStub
                = CPREEServiceGrpc.newBlockingStub(CPREEChannel.getInstance().getChannel());
        cpreeStub.modifyCPMonitor(au.coaas.cpree.proto.CPMonitor.newBuilder()
                .setContextEntity(entity).setContextID(contextId).setDelete(true).build());

        CQCServiceGrpc.CQCServiceBlockingStub cqcStub
                = CQCServiceGrpc.newBlockingStub(CQCChannel.getInstance().getChannel());
        cqcStub.removeMonitor(au.coaas.cqc.proto.CPMonitor.newBuilder()
                .setContextEntity(entity).setContextID(contextId).build());
    }

    // 'value' is exactly the same as 'ce'.
    private static boolean evaluateNonDeterministic(Map<String, JSONObject> value, Queue<CdqlConditionToken> RPNCondition) throws Exception {
        Stack<CdqlConditionToken> stack = new Stack<>();
        while (RPNCondition.size() > 0) {
            CdqlConditionToken token = RPNCondition.poll();
            switch (token.getType()) {
                case Operator:
                    String op = token.getStringValue();
                    CdqlConditionToken newToken = applyOperator(op, stack);
                    stack.push(newToken);
                    break;
                case Attribute:
                    String value_res = findAttributeValue(token, new JSONObject(value));
                    stack.push(CdqlConditionToken.newBuilder()
                            .setType(CdqlConditionTokenType.Constant)
                            .setStringValue(value_res)
                            .setConstantTokenType(getConstantType(value_res)).build());
                    break;
                case Function:
                    JSONArray func_result = PullBasedExecutor.executeFunction(token.getFunctionCall(), value, RPNCondition.poll().getStringValue(),
                            RPNCondition.poll().getStringValue());
                    if(func_result != null && !func_result.isEmpty()){
                        stack.push(CdqlConditionToken.newBuilder()
                                .setType(CdqlConditionTokenType.Constant)
                                .setConstantTokenType(CdqlConstantConditionTokenType.Array)
                                .setStringValue(func_result.toString())
                                .build());
                    }
                    break;
                default:
                    stack.push(token);
            }
        }
        try {
            return stack.size() == 1 && Boolean.valueOf(stack.pop().getStringValue());
        } catch (Exception e) {
            log.severe("Error in evaluate non-detterministic.");
        }
        return false;
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
                log.severe(e.getMessage());
            }
        }

        return constantType;
    }

    public static boolean evaluate(String subscriptionID, JSONObject values, JSONObject old,
                                   Queue<CdqlConditionToken> RPNCondition, CDQLQuery query, double complexity) throws WrongOperatorException {
        Stack<CdqlConditionToken> stack = new Stack<>();
        while (RPNCondition.size() > 0) {
            CdqlConditionToken token = RPNCondition.poll();
            switch (token.getType()) {
                case Operator:
                    String op = token.getStringValue();
                    CdqlConditionToken newToken = applyOperator(op, stack);
                    stack.push(newToken);
                    break;
                case Attribute:
                    String value = findAttributeValue(token, values);
                    stack.push(CdqlConditionToken.newBuilder()
                            .setType(CdqlConditionTokenType.Constant)
                            .setStringValue(value)
                            .setConstantTokenType(getConstantType(value)).build());
                    break;
                case Function:
                    String funcName = token.getFunctionCall().getFunctionName();
                    FunctionCall functionCall = preproccessFunctionCall(token.getFunctionCall(), values, old, query,
                            subscriptionID, funcName, complexity);
                    stack.push(CdqlConditionToken.newBuilder()
                            .setConstantTokenType(CdqlConstantConditionTokenType.Json)
                            .setType(CdqlConditionTokenType.Constant)
                            .setStringValue(executeFunctionCall(functionCall, subscriptionID, funcName, complexity, RPNCondition).toString())
                            .build());
                    break;
                default:
                    stack.push(token);
            }
        }

        try {
            return stack.size() == 1 && Boolean.valueOf(stack.pop().getStringValue());
        } catch (Exception e) {
            return false;
        }
    }

    private static CdqlConditionToken applyOperator(String operator, Stack<CdqlConditionToken> stack) throws WrongOperatorException {
        CdqlConditionToken operand_2 = stack.pop();
        CdqlConditionToken operand_1 = stack.pop();

        switch (operator) {
            case "and": {
                if(operand_1.getType() == operand_2.getType()){
                    if(operand_1.getConstantTokenType() == operand_2.getConstantTokenType()){
                        switch (operand_1.getConstantTokenType()){
                            case Numeric:
                            case Boolean:
                            case String:
                                return processConstantValues(operand_1, operand_2, operator);
                            case Array:
                                return intersection(new JSONArray(operand_1.getStringValue()),
                                        new JSONArray(operand_2.getStringValue()));
                            default:
                                throw new WrongOperatorException("Cannot apply AND operator for the given operands.");
                        }
                    }

                    if(operand_1.getConstantTokenType() == CdqlConstantConditionTokenType.Boolean ||
                            operand_2.getConstantTokenType() == CdqlConstantConditionTokenType.Boolean){
                        if(operand_2.getConstantTokenType() == CdqlConstantConditionTokenType.Boolean &&
                                Boolean.valueOf(operand_2.getStringValue())){
                            return operand_1;
                        }
                        if(operand_1.getConstantTokenType() == CdqlConstantConditionTokenType.Boolean &&
                                Boolean.valueOf(operand_1.getStringValue())){
                            return operand_2;
                        }

                        return CdqlConditionToken.newBuilder()
                                .setStringValue((new JSONArray()).toString())
                                .setType(operand_1.getType()).setConstantTokenType(operand_1.getConstantTokenType())
                                .setContextAttribute(operand_1.getContextAttribute())
                                .setContextEntity(operand_1.getContextEntity())
                                .setFunctionCall(operand_1.getFunctionCall()).build();
                    }
                }
                break;
            }
            case "or": {
                if(operand_1.getType() == operand_2.getType()){
                    if(operand_1.getConstantTokenType() == operand_2.getConstantTokenType()){
                        switch (operand_1.getConstantTokenType()){
                            case Numeric:
                            case Boolean:
                            case String:
                                return processConstantValues(operand_1, operand_2, operator);
                            case Array:
                                return union(new JSONArray(operand_1.getStringValue()),
                                        new JSONArray(operand_2.getStringValue()));
                            default:
                                throw new WrongOperatorException("Cannot apply AND operator for the given operands.");
                        }
                    }

                    if(operand_1.getConstantTokenType() == CdqlConstantConditionTokenType.Boolean ||
                            operand_2.getConstantTokenType() == CdqlConstantConditionTokenType.Boolean){
                        return operand_1;
                    }
                }
                break;
            }
            case "=": {
                if (operand_1.getType() == CdqlConditionTokenType.Constant &&
                        operand_2.getType() == CdqlConditionTokenType.Constant) {
                    return processConstantValues(operand_1, operand_2, operator);
                }

                if (operand_1.getType() != CdqlConditionTokenType.Attribute)
                    throw new WrongOperatorException("The first operand in the expression should be an attribute");

                if (operand_2.getType() != CdqlConditionTokenType.Constant)
                    throw new WrongOperatorException("The second operand in the expression should be a constant value");
                break;
            }
            default: {
                if (operand_1.getType() == CdqlConditionTokenType.Constant
                        && operand_2.getType() == CdqlConditionTokenType.Constant) {
                    return processConstantValues(operand_1, operand_2, operator);
                }
                if (operand_1.getType() != CdqlConditionTokenType.Attribute)
                    throw new WrongOperatorException("the first operand in the expression should be an attribute");

                if (operand_2.getType() != CdqlConditionTokenType.Constant)
                    throw new WrongOperatorException("the second operand in the expression should be a constant value");
                break;
            }
        }
        return null;
    }

    private static CdqlConditionToken intersection(JSONArray results1, JSONArray results2) throws JSONException {
        Set<JSONObject> firstAsSet = convertJSONArrayToSet(results1);
        Set<JSONObject> secondIdsAsSet = convertJSONArrayToSet(results2);
        Set<JSONObject> intersection = firstAsSet.stream()
                .distinct()
                .filter(secondIdsAsSet::contains)
                .collect(Collectors.toSet());

        CdqlConditionToken dataToken = CdqlConditionToken.newBuilder()
                .setType(CdqlConditionTokenType.Constant)
                .setConstantTokenType(CdqlConstantConditionTokenType.Array)
                .setStringValue(intersection.toArray().toString())
                .build();

        return dataToken;
    }

    private static CdqlConditionToken union(JSONArray results1, JSONArray results2){
        Set<JSONObject> firstAsSet = convertJSONArrayToSet(results1);
        Set<JSONObject> secondIdsAsSet = convertJSONArrayToSet(results2);
        Set<JSONObject>  union = Stream.concat(firstAsSet.stream(), secondIdsAsSet.stream())
                .collect(Collectors.toSet());

        CdqlConditionToken dataToken = CdqlConditionToken.newBuilder()
                .setType(CdqlConditionTokenType.Constant)
                .setConstantTokenType(CdqlConstantConditionTokenType.Array)
                .setStringValue(union.toArray().toString())
                .build();

        return dataToken;
    }

    private static Set<JSONObject> convertJSONArrayToSet(JSONArray input) throws JSONException {
        Set<JSONObject> retVal = new HashSet<>();
        for (Object item: input) {
            retVal.add((JSONObject) item);
        }
        return retVal;
    }

    private static CdqlConditionToken processConstantValues(CdqlConditionToken operand1, CdqlConditionToken operand2, String operator) throws WrongOperatorException {

        CdqlConditionToken tokenTrue = CdqlConditionToken.newBuilder()
                .setType(CdqlConditionTokenType.Constant)
                .setStringValue("false")
                .setConstantTokenType(CdqlConstantConditionTokenType.Boolean)
                .build();

        CdqlConditionToken tokenFalse = CdqlConditionToken.newBuilder()
                .setType(CdqlConditionTokenType.Constant)
                .setStringValue("true")
                .setConstantTokenType(CdqlConstantConditionTokenType.Boolean)
                .build();

        if (operand1.getConstantTokenType() == CdqlConstantConditionTokenType.Numeric) {
            int res = Double.valueOf(operand1.getStringValue())
                    .compareTo(Double.valueOf(operand2.getStringValue()));
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
        }
        else if (operand1.getConstantTokenType() == CdqlConstantConditionTokenType.Json) {
            JSONObject json = new JSONObject(operand1.getStringValue());
            if(json.has("results")) return tokenFalse;

            Double value = json.optDouble("value");
            int res = value.compareTo(Double.valueOf(operand2.getStringValue()));
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
        }
        else {
            switch (operator) {
                case "=":
                    return operand1.getStringValue().equals(operand2.getStringValue()) ?
                            tokenTrue : tokenFalse;
                case "!=":
                    return !operand1.getStringValue().equals(operand2.getStringValue()) ?
                            tokenTrue : tokenFalse;
            }
        }

        throw new WrongOperatorException("Wrong operator provided.");
    }

    private static String findAttributeValue(CdqlConditionToken cdqlContextAttributeConditionToken, JSONObject values) {
        // Getting the entity JSON from the values using the entity ID (here, entity name is the entity id).
        JSONObject entity = values.getJSONObject(cdqlContextAttributeConditionToken.getContextAttribute().getEntityName());
        String[] split = cdqlContextAttributeConditionToken.getContextAttribute().getAttributeName().contains(".") ?
                cdqlContextAttributeConditionToken.getContextAttribute().getAttributeName().split("\\.") :
                new String[] {cdqlContextAttributeConditionToken.getContextAttribute().getAttributeName()};
        JSONObject item = entity;
        for (int i = 0; i < split.length - 1; i++) {
            item = item.getJSONObject(split[i]);
        }
        String value = String.valueOf(item.get(split[split.length - 1]));
        return value;
    }

    private static Gson gson = new Gson();

    private static FunctionCall preproccessFunctionCall(FunctionCall fCall, JSONObject jsonValues, JSONObject old, CDQLQuery query,
                                                        String subID, String stringFcall, double complexity) {
        FunctionCall.Builder fCallTemp = FunctionCall.newBuilder()
                .setFunctionName(fCall.getFunctionName());
        AtomicInteger index = new AtomicInteger();
        fCall.getSubItemsList().forEach(item -> {
            fCallTemp.setSubItems(index.get(), item);
            index.getAndIncrement();
        });

        int iter_index = 0;
        for (Operand argument : fCall.getArgumentsList()) {
            if (null != argument.getType()) {
                switch (argument.getType()) {
                    case FUNCTION_CALL: {
                        String funName = argument.getFunctioncall().getFunctionName();
                        FunctionCall subFunction = preproccessFunctionCall(gson.fromJson(gson.toJson(argument.getStringValue()), FunctionCall.class),
                                jsonValues, old, query, subID, stringFcall, complexity);
                        fCallTemp.setArguments(iter_index, Operand.newBuilder()
                                        .setType(OperandType.CONTEXT_VALUE_STRING)
                                        .setContextAttribute(argument.getContextAttribute())
                                        .setStringValue(executeFunctionCall(subFunction, subID, stringFcall, complexity, null).toString())
                                        .build());
                        break;
                    }
                    case CONTEXT_ATTRIBUTE:
                        if (fCall.getFunctionName().equals("change")) {
                            try {
                                JSONObject values = new JSONObject(argument.getStringValue());
                                JSONObject entity = old.getJSONObject(values.get("entityName").toString());
                                String[] split = (values.get("attributeName").toString()).split("\\.");
                                if (entity.has("results")) {
                                    entity = entity.getJSONArray("results").getJSONObject(0);
                                }

                                JSONObject item = new JSONObject(entity.toString());
                                for (int i = 0; i < split.length - 1; i++) {
                                    item = item.getJSONObject(split[i]);
                                }

                                String value = String.valueOf(item.get(split[split.length - 1]));
                                fCallTemp.setArguments(iter_index, Operand.newBuilder()
                                        .setContextAttribute(argument.getContextAttribute())
                                        .setType(OperandType.CONTEXT_VALUE_STRING)
                                        .setStringValue(value).build());
                            } catch (Exception e) {
                                fCallTemp.setArguments(iter_index, Operand.newBuilder()
                                        .setContextAttribute(argument.getContextAttribute())
                                        .setType(OperandType.CONTEXT_VALUE_STRING)
                                        .setStringValue(null).build());
                            }
                        }

                        JSONObject values;
                        JSONObject values_org = new JSONObject(argument.getStringValue());

                        if(values_org.has("results")) values = values_org.getJSONArray("results").getJSONObject(0);
                        else values = values_org;

                        ContextAttribute con_attr = argument.getContextAttribute();
                        String[] split = con_attr.getAttributeName().split("\\.");
                        for (int i = 0; i < split.length - 1; i++) {
                            values = values.getJSONObject(split[i]);
                        }

//                        JSONObject values = new JSONObject(argument.getStringValue());
//                        JSONObject entity = jsonValues.getJSONObject(values.get("entityName").toString());
//                        String[] split = ((String) values.get("attributeName").toString()).split("\\.");
//                        JSONObject item = entity;
//                        if (item.has("results")) {
//                            item = item.getJSONArray("results").getJSONObject(0);
//                        }
//                        for (int i = 0; i < split.length - 1; i++) {
//                            item = item.getJSONObject(split[i]);
//                        }

                        String value = String.valueOf(values.get(split[split.length - 1]));
                        if ((fCall.getFunctionName().equals("decrease") || fCall.getFunctionName().equals("increase") || fCall.getFunctionName().equals("distance"))
                        && (split[split.length - 1].equals("location") || split[split.length - 1].equals("geo"))) {
                            JSONObject val = new JSONObject(value);
                            val.put("speed", values_org.has("speed") ? values_org.getDouble("speed") :
                                    con_attr.getEntityName().equals("car") ? 60 : 5);
                            val.put("heading", values_org.has("heading") ? values_org.getDouble("heading") : 0);
                        }

                        fCallTemp.setArguments(iter_index, Operand.newBuilder()
                                .setContextAttribute(argument.getContextAttribute())  // Adding it here to indicate value was derived as a context attribute.
                                .setType(OperandType.CONTEXT_VALUE_STRING)
                                .setStringValue(value).build());
                        break;
                    case CONTEXT_ENTITY:
                        ContextEntity ce = gson.fromJson(argument.getStringValue(), ContextEntity.class);
                        String entityID = ce.getEntityID();
                        JSONObject operandEntity = jsonValues.getJSONObject(entityID);
                        Optional<ContextEntity> findEntity = query.getDefine().getDefinedEntitiesList()
                                .stream().filter(x -> x.getEntityID().equals(entityID)).findAny();
                        String entityTypeString = findEntity.get().getType().toString();
                        operandEntity.put("entityType", entityTypeString);
                        fCallTemp.setArguments(iter_index, Operand.newBuilder()
                                .setContextEntity(findEntity.get()) // Adding it here because, in order to be a JSON, it has to be an entity.
                                .setType(OperandType.CONTEXT_VALUE_JSON)
                                .setStringValue(operandEntity.toString()).build());
                        break;
                    default:
                        fCallTemp.setArguments(iter_index, argument);
                        break;
                }
            }
            iter_index++;
        }
        return fCallTemp.build();
    }

    private static JSONObject executeFunctionCall(FunctionCall fCall, String subID, String stringFcall,
                                                  double complexity, Queue<CdqlConditionToken> comparators) {
        try {
            if (fCall.getFunctionName().equals("change")) {
                String val1 = fCall.getArguments(0).getStringValue();
                String val2 = fCall.getArguments(1).getStringValue();
                if (val1 == null || !val1.equals(val2)) {
                    if (fCall.getArgumentsCount() > 2) {
                        String val3 = fCall.getArguments(2).getStringValue();
                        try {
                            if (!Double.valueOf(val3).equals(Double.valueOf(val2))) {
                                return (new JSONObject()).put("value", "false");
                            }
                        } catch (Exception e) {
                            if (!(val2.replaceAll("\"", "").contains(val3.replaceAll("\"", "")))
                                    && !val2.replaceAll("\"", "").equals(val3.replaceAll("\"", ""))) {
                                return (new JSONObject()).put("value", "false");
                            }
                        }
                    }
                    return (new JSONObject()).put("value", "true");
                }
                return (new JSONObject()).put("value", "false");
            } else if (fCall.getFunctionName().equals("timeDifference")) {
                try {
                    SimpleDateFormat format
                            = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                    String time1 = fCall.getArguments(0).getStringValue();
                    if (time1.trim().startsWith("{")) {
                        time1 = new JSONObject(time1).get("value").toString();
                    }
                    String time2 = fCall.getArguments(1).getStringValue();
                    if (time2.trim().startsWith("{")) {
                        time2 = new JSONObject(time2).get("value").toString();
                    }

                    DateTime date1 = null;
                    try {
                        date1 = new DateTime(Long.valueOf(time1));
                    } catch (Exception e) {
                        date1 = new DateTime(format.parse(time1));
                    }
                    DateTime date2 = null;
                    try {
                        date2 = new DateTime(Long.valueOf(time2));
                    } catch (Exception e) {
                        date2 = new DateTime(format.parse(time2));
                    }

                    return new JSONObject("{\"value\":" + new Period(date1, date2).getMinutes() + "}");
                } catch (Exception e) {
                    return new JSONObject("{\"value\":false}");
                }
            } else if (fCall.getFunctionName().equals("currentTime")) {
                String timeZone = fCall.getArguments(0).getStringValue().replaceAll("\"", "");
                DateTime dt = new DateTime(new Date());
                DateTimeZone dtZone = DateTimeZone.forID(timeZone);
                DateTime dtus = dt.withZone(dtZone);
                return new JSONObject("{\"value\":" + dtus.getMillis() + "}");
            } else if (fCall.getFunctionName().equals("decrease") || fCall.getFunctionName().equals("increase")
                    || fCall.getFunctionName().equals("isValid")) {
                String value = fCall.getArguments(0).getStringValue();

                ContextEvent ev = ContextEvent.newBuilder()
                        .setSubscriptionID(subID)
                        .setSubscriptionValue(value)
                        .setTimestamp(String.valueOf(System.currentTimeMillis()))
                        .setFunctionString(stringFcall)
                        .setFunctionName(fCall.getFunctionName())
                        .build();

                PushBasedExecutor.sendEvent(ev);

                // This should be sent back to the CRE to trigger the Siddhi Manager and get the results
                CREServiceGrpc.CREServiceBlockingStub stub
                        = CREServiceGrpc.newBlockingStub(CREChannel.getInstance().getChannel());
                CRESituation res = stub.getSiddhiResults(ev);

                if(res.getStatus().equals("200") && res.getBody() != null){
                    String event = res.getBody().replace("[", "").replace("]", "");
                    String[] split = event.split(",");
                    List<String> ev_list = Arrays.asList(split);

                    // Need to find what this calculation is. Seems like a polling time.
                    if (Long.valueOf(ev_list.get(3)) >= (System.currentTimeMillis() - Double.valueOf(fCall.getArguments(1).getStringValue()) * 60000)) {
                        if (fCall.getArgumentsCount() > 2) {
                            double delta = Double.valueOf(fCall.getArguments(2).getStringValue());
                            if (delta <= Math.abs(Double.valueOf(ev_list.get(2)) - Double.valueOf(ev_list.get(1)))) {
                                return new JSONObject("{\"value\":true}");
                            }
                        } else {
                            return new JSONObject("{\"value\":true}");
                        }
                    }
                }
                return new JSONObject("{\"value\":false}");
            }
            else {
                // Any other situational function
                SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                        = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
                SQEMResponse slaMessage = sqemStub.getConsumerSLA(AuthToken.newBuilder().setUsername(subID).build());
                if(slaMessage.getStatus().equals("200")) {
                    JSONObject sla = new JSONObject(slaMessage.getBody());
                    String consumerId = sla.getJSONObject("_id").getString("$oid");
                    Object execute = PullBasedExecutor.executeSituationFunction(fCall, complexity, consumerId);

                    JSONObject cacheable_situ = new JSONObject();
                    if (execute.toString().trim().startsWith("{")) {
                        cacheable_situ = (JSONObject) execute;
                        // cacheable_situ.put("results", new JSONArray(execute.toString().replaceAll("\"", "")));
                    } else cacheable_situ.put("value", (Double) execute);

                    // Start predicting for this situational function.
                    JSONObject finalCacheable_situ = cacheable_situ;
                    new Thread(() -> {
                        if(comparators != null) {
                            CdqlConditionToken[] preds = predictSituation(consumerId, fCall.getFunctionName());
                            CdqlConditionToken operand_2 = comparators.poll();
                            CdqlConditionToken operator = comparators.poll();

                            for(CdqlConditionToken pred_value : preds) {
                                Stack<CdqlConditionToken> stack = new Stack<>();
                                stack.push(pred_value);
                                stack.push(operand_2);
                                try {
                                    CdqlConditionToken newToken = applyOperator(operator.getStringValue(), stack);
                                    // Starting to proactively cache.
                                    if(newToken.getStringValue().equals("true")) {
                                        // Check if this is a JSON (hence, an entity been passed an argument).
                                        if(cacheEnabled){
                                            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub_caching
                                                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
                                            // Firstly, caching the situation to be refreshed reactively.
                                            String situId = Utilities.combineHashKeys(fCall.getArgumentsList().stream().map(x -> {
                                                // This is a quick fix to defining a hashkey for the situations.
                                                String strValue = x.getStringValue();
                                                if(strValue.startsWith("{")) return (new JSONObject(strValue)).getString("hashkey");
                                                else return strValue;
                                            }).collect(Collectors.toList()));

                                            sqemStub_caching.cacheContext(CacheRequest.newBuilder()
                                                    .setSituReference(SituationLookUp.newBuilder()
                                                            .setFunction(fCall)
                                                            .setUniquehashkey(situId)
                                                            .setZeroTime(System.currentTimeMillis()).build())
                                                    .setJson(finalCacheable_situ.toString())
                                                    .setCacheLevel(CacheLevels.SITU_FUNCTION.toString())
                                                    .setRefreshLogic("REACTIVE")
                                                    .setHashkey(situId)
                                                    .setIndefinite(true)
                                                    .build());

                                            // Secondly, cache the expected context (path). This happends through the same loop.
                                            // Not caching further context entities becuase,for example, car may not be cached cecause it is cost inefficient to
                                            // refresh the location all the time.
                                            for(Operand x : fCall.getArgumentsList()) {
                                                String refAttr = "location";
                                                if(x.getStringValue().startsWith("{")){
                                                    // It is always implicit that when a context entity is mobile, it generates JSON type data.
                                                    // So, considering only CONTEXT_VALUE_JSON for processing.
                                                    JSONObject argValue = new JSONObject(x.getStringValue());
                                                    if(argValue.has("mobile") && argValue.getBoolean("mobile")) {
                                                        double lat = 0, lng = 0;
                                                        if(argValue.has("latitude") && argValue.has("longitude")) {
                                                            lat = argValue.getDouble("latitude");
                                                            lng = argValue.getDouble("longitude");
                                                        } else {
                                                            for(String att : locAttrs) {
                                                                refAttr = att;
                                                                if(argValue.has(att)) {
                                                                    lat = argValue.getJSONObject(att).getDouble("latitude");
                                                                    lng = argValue.getJSONObject(att).getDouble("longitude");
                                                                }
                                                            }
                                                        }

                                                        GeoIndexer geo = GeoIndexer.getInstance();
                                                        // TODO: Should be best based on historic path analysis.
                                                        Path pred_path = geo.getPredictedPath(lat, lng, argValue.getDouble("heading"),
                                                                argValue.getDouble("speed"), 3);

                                                        sqemStub_caching.cacheContext(CacheRequest.newBuilder()
                                                                .setJson(JsonFormat.printer().print(pred_path))
                                                                .setCacheLevel(CacheLevels.ATTRIBUTE.toString())
                                                                .setReference(CacheLookUp.newBuilder()
                                                                        .setKey(refAttr)
                                                                        .setZeroTime(pred_path.getZeroTime())
                                                                        .setEt(x.getContextEntity().getType())
                                                                        .setServiceId(argValue.getJSONArray("providers").getString(0))
                                                                        .setHashKey(argValue.getString("hashkey")).build())
                                                                .setIndefinite(false) // Should be set to Entity lifetime if available or indefinite (if the ghost transforms, then take that lifetime).
                                                                .setHashkey("pred_path")
                                                                .build());
                                                    }
                                                }
                                                else if ((fCall.getFunctionName().equals("distance") || fCall.getFunctionName().equals("increase")
                                                    || fCall.getFunctionName().equals("decrease")) && x.getType().equals(OperandType.CONTEXT_VALUE_STRING.toString())) {
                                                    if(x.hasContextAttribute() && Arrays.asList(locAttrs).contains(x.getContextAttribute().getAttributeName())) {
                                                        JSONObject argValue = new JSONObject(x.getStringValue());
                                                        double lat = 0, lng = 0;
                                                        for(String att : locAttrs) {
                                                            if(argValue.has(att)) {
                                                                lat = argValue.getJSONObject(att).getDouble("latitude");
                                                                lng = argValue.getJSONObject(att).getDouble("longitude");
                                                            }
                                                        }
                                                        GeoIndexer geo = GeoIndexer.getInstance();
                                                        Path pred_path = geo.getPredictedPath(lat, lng, argValue.getDouble("heading"),
                                                                argValue.getDouble("speed"), 3);

                                                        sqemStub_caching.cacheContext(CacheRequest.newBuilder()
                                                                .setJson(JsonFormat.printer().print(pred_path))
                                                                .setCacheLevel(CacheLevels.ATTRIBUTE.toString())
                                                                .setReference(CacheLookUp.newBuilder()
                                                                        .setKey(refAttr)
                                                                        .setZeroTime(pred_path.getZeroTime())
                                                                        .setEt(x.getContextEntity().getType())
                                                                        .setServiceId(argValue.getJSONArray("providers").getString(0))
                                                                        .setHashKey(argValue.getString("hashkey")).build())
                                                                .setIndefinite(false) // Should be set to Entity lifetime
                                                                .setHashkey("pred_path")
                                                                .build());
                                                    }
                                                }
                                            }
                                        }

                                        log.info("Situation predicted and cached.");
                                        break; // Immediatly breaking from the loop because we already know the situation is expected to happen.
                                    }
                                }
                                catch (Exception ex) {
                                    log.info("Error when apploying operators - either premature or wrong use of operation.");
                                    log.info(ex.getMessage());
                                }
                            }
                        }
                    }).start();

                    return cacheable_situ;
                }
            }

            JSONObject result = new JSONObject();

            if (fCall.getSubItemsList().isEmpty()) {
                return result;
            }

            List<String> subItems = fCall.getSubItemsList();
            for (int i = 0; i < subItems.size() - 1; i++) {
                result = result.getJSONObject(subItems.get(i));
            }

            Object res = result.get(subItems.get(subItems.size() - 1));
            if (res instanceof JSONObject) {
                JSONObject tempJO = (JSONObject) res;
                if (tempJO.has("value")) {
                    return (new JSONObject()).put("value", tempJO.get("value"));
                }
            }
            return (new JSONObject()).put("value", res);
        }
        catch (IOException | NumberFormatException | JSONException e) {
            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            SQEMResponse slaMessage = sqemStub.getConsumerSLA(AuthToken.newBuilder().setUsername(subID).build());

            // TODO: What to do when there is no consumer found for the given subscription ID?
            JSONObject sla = new JSONObject(slaMessage.getBody());
            Object execute = PullBasedExecutor.executeSituationFunction(fCall, complexity,
                    sla.getJSONObject("_id").getString("$oid"));

            if (execute.toString().trim().startsWith("{")) {
                return (JSONObject) execute;
                // return (new JSONObject()).put("results",
                //         new JSONArray(execute.toString().replaceAll("\"", "")));
            } else {
                return (new JSONObject()).put("value", execute);
            }
        }
    }

    public static Path processPredictedPath (PathRequest request) throws InvalidProtocolBufferException {
        GeoIndexer geo = GeoIndexer.getInstance();
        Path pred_path = geo.getPredictedPath(request.getLatitude(), request.getLongitude(),
                request.getHeading(), request.getSpeed(), 3);
        if(request.getRequest().isInitialized()) {
            CacheLookUp cacheRef = request.getRequest().getReference()
                    .toBuilder().setZeroTime(System.currentTimeMillis())
                    .build();

            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            sqemStub.refreshContextEntity(request.getRequest().toBuilder()
                    .setJson(JsonFormat.printer().print(pred_path))
                    .setReference(cacheRef).build());
        }

        return pred_path;
    }

    public static EventStats getEventHandlingStats() {
        return EventStats.newBuilder()
               .setAvgExecutionTime(totalExecutionTime/totalNumberOfEvents)
               .setEventsProcessed(totalNumberOfEvents).build();
    }

    public static Empty restartMonitoring() {
        totalNumberOfEvents = 0;
        totalExecutionTime = 0;
        return Empty.newBuilder().build();
    }

    // Start predicting the situation.
    public static CdqlConditionToken[] predictSituation(String consumerId, String situationName) {
        String predictURI = String.format("http://localhost:9797/predictions?consumer=%1$s&situation=%2$s",
                consumerId, situationName);
        String predictions = Utilities.httpCall(predictURI, HttpRequests.GET, RequestDataType.JSON, null, null);
        if(predictions != null){
            JSONObject pred_res = new JSONObject(predictions);
            JSONArray res = pred_res.getJSONArray("predictions");
            CdqlConditionToken[] pred_values = new CdqlConditionToken[res.length()];
            for (int i = 0; i < res.length(); ++i) {
                pred_values[i] = CdqlConditionToken.newBuilder().setStringValue(res.getString(i))
                        .setType(CdqlConditionTokenType.Constant)
                        .setConstantTokenType(CdqlConstantConditionTokenType.Numeric)
                        .build();
            }
            return pred_values;
        }
        return null;
    }

    private static void recordContextPush (JSONObject cars, String consumerId, String bikeId) {
        SQEMServiceGrpc.SQEMServiceFutureStub sqemStub
                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
        // Only recording the cars as for the 'hazardous' scenario.
        JSONObject car_context = cars.getJSONObject("car");
        if(!car_context.has("results")) {
            JSONArray resSet = new JSONArray();
            resSet.put(car_context);
            car_context = new JSONObject();
            car_context.put("results", resSet);
        }
        sqemStub.logPushResponse(ContextResponse.newBuilder()
                .setJson(car_context.toString()).setConsumerId(consumerId)
                .setRecieverId(bikeId).build());
    }
}
