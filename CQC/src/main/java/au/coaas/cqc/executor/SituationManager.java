package au.coaas.cqc.executor;

import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.Empty;
import au.coaas.cqc.proto.EventRequest;
import au.coaas.cqc.proto.EventStats;
import au.coaas.cqc.utils.exceptions.WrongOperatorException;

import au.coaas.cqp.proto.*;

import au.coaas.cre.proto.CRESituation;
import au.coaas.cre.proto.ContextEvent;
import au.coaas.cre.proto.CREServiceGrpc;
import au.coaas.cre.proto.SituationRequest;

import au.coaas.grpc.client.CREChannel;
import au.coaas.grpc.client.SQEMChannel;

import au.coaas.sqem.proto.Situation;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.CdqlSubscription;
import au.coaas.sqem.proto.SituationFunctionRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import com.google.gson.Gson;
import org.joda.time.Period;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;

import java.util.*;
import java.text.SimpleDateFormat;

import java.util.stream.Stream;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class SituationManager {
    public static long totalExecutionTime = 0;
    public static long totalNumberOfEvents = 0;

    private static Logger log = Logger.getLogger(SituationManager.class.getName());

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
                    .setProviderID(eventRequest.getProvider())
                    // Following are commented because they should be null.
                    // .setSubscriptionID(persEvent.getString("subscriptionID"))
                    // .setSubscriptionValue(persEvent.getString("subscriptionValue"))
                    .setTimestamp(persEvent.getString("timestamp"))
                    .setContextEntity(mapper.readValue(persEvent.getJSONObject("contextEntity").toString(),
                            ContextEntity.class));
            if(persEvent.has("attributes"))
                eventBuilder.setAttributes(persEvent.getJSONObject("attributes").toString());

            ContextEvent event = eventBuilder.build();

            List<SubscribedQuery> subQueries = getSubscribedQueries(event);

            JSONObject result = new JSONObject();
            JSONArray pushList = new JSONArray();

            for (SubscribedQuery subscription : subQueries) {
                boolean notRelated = true;
                for (ContextEntity subEntity : subscription.getRelatedEntitiesList()) {
                    Queue<CdqlConditionToken> tempEntityQueue = new LinkedList<>(subEntity.getCondition().getRPNConditionList());
                    if (event.getContextEntity().getType().getType().equals(subEntity.getType().getType())) {
                        JSONObject tempEventJson = persEvent.getJSONObject("attributes");
                        tempEventJson.put(event.getKey(), event.getContextEntity().getEntityID());
                        if (evaluateNonDeterministic(new JSONObject().put(subEntity.getEntityID(), tempEventJson), tempEntityQueue) == true) {
                            notRelated = false;
                            break;
                        }
                    }
                }

                if (notRelated) continue;

                CdqlResponse pullResponse = PullBasedExecutor.executePullBaseQuery(subscription.getQuery(),
                        subscription.getToken(), -1, -1, subscription.getQueryId(),
                        subscription.getCriticality(), subscription.getComplexity());

                String body = pullResponse.getBody();
                JSONObject jsonObject = new JSONObject(body);
                String entityID = subscription.getRelatedEntities(0).getEntityID();
                JSONObject oldValue = new JSONObject(jsonObject.toString());

                try {
                    JSONObject ent = jsonObject.getJSONObject(subscription.getRelatedEntities(0).getEntityID());
                    if (ent.has("results")) {
                        ent = ent.getJSONArray("results").getJSONObject(0);
                    }
                    if (!ent.get(event.getKey()).toString().equals(event.getContextEntity().getEntityID())) {
                        continue;
                    }

                } catch (Exception e) {
                    continue;
                }

                for (String key : jsonObject.keySet()) {
                    try {
                        jsonObject.put(key, jsonObject.getJSONObject(key)
                                .getJSONArray("results").getJSONObject(0));
                    } catch (Exception e) {

                    }
                }

                for (Map.Entry<String, String> entry : event.getContextEntity().getContextAttributesMap().entrySet()) {
                    String[] keys = entry.getKey().split(":");
                    String key = keys[keys.length - 1];
                    Object value = persEvent.getJSONObject("attributes").get(key);
                    jsonObject.getJSONObject(entityID).put(key, value);
                }

                Queue<CdqlConditionToken> tempQueue = new LinkedList<>(subscription.getSituationList());
                boolean flag = false;
                try {
                    flag = evaluate(subscription.getId(), jsonObject, oldValue, tempQueue,
                            subscription.getQuery(), subscription.getComplexity());
                } catch (Exception ex) {
                    log.severe("Error occured when evaluating the situations: " + ex.getMessage());
                    log.severe(String.valueOf(ex.getStackTrace()));
                }

                if (flag) {
                    pushList.put(jsonObject);
                    try {
                       PushBasedExecutor.pushContext(subscription.getCallback(), jsonObject, subscription.getId());
                    } catch (InterruptedException | ExecutionException ex) {
                        log.severe(ex.getMessage());
                    } catch (IOException ex) {
                        log.severe(ex.getMessage());
                    }
                }
            }

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
        CdqlSubscription subs_res = sqemStub.getRelatedSubscriptions(Situation.newBuilder()
                .setContextEntity(event.getContextEntity())
                .setProviderID(event.getProviderID())
                // Following are commented because they should be null.
                // .setSubscriptionID(event.getSubscriptionID())
                // .setSubscriptionValue(event.getSubscriptionValue())
                .setTimestamp(event.getTimestamp()).build());

        if(subs_res.getStatus().equals("200")){
            JSONArray subs = new JSONArray(subs_res.getBody());
            for (int i = 0; i < subs.length(); i++) {
                JSONObject subitem = subs.getJSONObject(i);
                subitem.put("id", subitem.getJSONObject("_id").getString("$oid"));
                subitem.remove("_id");
            }

            return mapper.readValue(subs.toString(), new TypeReference<List<SubscribedQuery>>(){});
        }

        // TODO: Should unsubscribe from the event since there are no context queries that are interested in the event.
        log.info("No relevant subscriptions found");
        return null;
    }

    private static boolean evaluateNonDeterministic(JSONObject values, Queue<CdqlConditionToken> RPNCondition) throws WrongOperatorException {
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
                    stack.push(CdqlConditionToken.newBuilder()
                                    .setConstantTokenType(CdqlConstantConditionTokenType.String)
                                    .setType(CdqlConditionTokenType.Constant)
                                    .setStringValue("maybe").build());
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
                            .setStringValue(executeFunctionCall(functionCall, subscriptionID, funcName, complexity).toString())
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
        } else {
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
        JSONObject entity = values.getJSONObject(cdqlContextAttributeConditionToken.getContextAttribute().getEntityName());
        String[] split = cdqlContextAttributeConditionToken.getContextAttribute().getAttributeName().split("\\.");
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
                        FunctionCall subFunction = preproccessFunctionCall(gson.fromJson(gson.toJson(argument.getStringValue()), FunctionCall.class),
                                jsonValues, old, query, subID, stringFcall, complexity);
                        fCallTemp.setArguments(iter_index, Operand.newBuilder()
                                        .setType(OperandType.CONTEXT_VALUE_STRING)
                                        .setContextAttribute(argument.getContextAttribute())
                                        .setStringValue(executeFunctionCall(subFunction, subID, stringFcall, complexity).toString())
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

                        JSONObject values = new JSONObject(argument.getStringValue());
                        JSONObject entity = jsonValues.getJSONObject(values.get("entityName").toString());
                        String[] split = ((String) values.get("attributeName").toString()).split("\\.");
                        JSONObject item = entity;
                        if (item.has("results")) {
                            item = item.getJSONArray("results").getJSONObject(0);
                        }

                        for (int i = 0; i < split.length - 1; i++) {
                            item = item.getJSONObject(split[i]);
                        }

                        String value = String.valueOf(item.get(split[split.length - 1]));
                        fCallTemp.setArguments(iter_index, Operand.newBuilder()
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

    private static JSONObject executeFunctionCall(FunctionCall fCall, String subID, String stringFcall, double complexity) {
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
            Object execute = PullBasedExecutor.executeSituationFunction(fCall, complexity);

            if (execute.toString().trim().startsWith("[")) {
                return (new JSONObject()).put("results",
                        new JSONArray(execute.toString().replaceAll("\"", "")));
            } else {
                return (new JSONObject()).put("value", execute);
            }
        }
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
}
