package au.coaas.cqc.executor;

import au.coaas.cqc.grpc.client.CQPChannel;
import au.coaas.cqc.grpc.client.SQEMChannel;
import au.coaas.cqc.proto.*;
import au.coaas.cqp.proto.*;
import au.coaas.sqem.proto.ContextRequest;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import com.google.gson.Gson;
import org.json.JSONArray;
import org.json.JSONObject;

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

    public static CdqlResponse executePullBaseQuery(String cdql) {

        //parse the incoming query
        CQPServiceGrpc.CQPServiceBlockingStub stub
                = CQPServiceGrpc.newBlockingStub(CQPChannel.getInstance().getChannel());
        CDQLQuery query = (CDQLQuery) stub.parse(ParseRequest.newBuilder().setCdql(cdql).build());

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
                            terms.put("{" + key + "}", pathEncode);
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
                                        terms.put("{" + key + "." + jsonKey + "}", pathEncode);
                                    }
                                }
                            } else {
                                String temp = String.valueOf(get).replaceAll("\"", "");
                                String pathEncode = URLEncoder.encode(temp, java.nio.charset.StandardCharsets.UTF_8.toString());
                                terms.put("{" + key + "}", pathEncode);

                            }

                        } else {
                            String pathEncode = URLEncoder.encode(value, java.nio.charset.StandardCharsets.UTF_8.toString());
                            terms.put("{" + key + "}", pathEncode);
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
                String serviceURI = ContextServiceDiscovery.discover(entity.getType().getType(), terms.keySet());
                if (serviceURI == null) {
                    ce.put(entity.getEntityID(), executeSQEMQuery(entity, query, ce));
                    continue;
                }
                //ToDo function call and discovery
            }
        }


        JSONObject result = new JSONObject();

        for (Map.Entry<String, ListOfContextAttribute> entry : query.getSelect().getSelectAttrsMap().entrySet()) {
            String key = entry.getKey();
            result.put(key, ce.get(key));
        }
        //ToDo function call

        //String queryOuptput = OutputHandler.handle(result, query.getConfig().getOutputConfig());

        CdqlResponse cdqlResponse =  CdqlResponse.newBuilder().setStatus("200").setBody(result.toString()).build();


        return cdqlResponse;
    }


    private static JSONObject executeSQEMQuery(ContextEntity targetEntity,  CDQLQuery query, Map<String, JSONObject> ce) {
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        ArrayList<CdqlConditionToken> list = new ArrayList(targetEntity.getCondition().getRPNConditionList());

        for (int i = 0; i < list.size(); i++) {
            CdqlConditionToken item = list.get(i);

            switch (item.getType()) {
                case Function:
                    FunctionCall.Builder fCall= item.getFunctionCall().toBuilder();
                    int requiredArgs = fCall.getArgumentsList().size();
                    for (int j = 0; j < fCall.getArgumentsList().size(); j++) {
                        Operand argument = fCall.getArgumentsList().get(j);
                        if (argument.getType() == OperandType.CONTEXT_ENTITY) {
                            ContextEntity object = argument.getContextEntity();
                            String entityID = object.getEntityID();
                            if (!entityID.equals(targetEntity.getEntityID())) {
                                ContextEntity findEntity = query.getDefine().getDefinedEntitiesList().stream().filter(e->e.getEntityID().equals(entityID)).findFirst().get();
                                String entityTypeString = findEntity.getType().getType();
                                JSONObject operandEntity = ce.get(entityID);
                                operandEntity.put("entityType", entityTypeString);
                                String handelQuantitativeValue = handelQuantitativeValue(operandEntity.toString());
                                fCall.addArguments(j,  Operand.newBuilder().setType(OperandType.CONTEXT_VALUE_JSON).setStringValue(handelQuantitativeValue));
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

                        CdqlConditionToken constantToken =  CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Constant)
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
                        CdqlConditionToken constantToken =  CdqlConditionToken.newBuilder().setType(CdqlConditionTokenType.Constant)
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

    public static void main (String [] args)
    {
        CdqlResponse res = PullBasedExecutor.executePullBaseQuery("prefix swm:http://swm.schema.org, \n" +
                "prefix schema:http://schema.org \n" +
                "pull (targetBin.*) \n" +
                "define \n" +
                "entity targetBin is from schema:SmartWasteContainer where \n" +
                "targetBin.Capacity.realTimeCapacity < 100 \n" +
                "and targetBin.AllowedWaste containsAny [\"swm:Plastic\",\"swm:green\"]");

        System.out.print(res.toString());
    }
}
