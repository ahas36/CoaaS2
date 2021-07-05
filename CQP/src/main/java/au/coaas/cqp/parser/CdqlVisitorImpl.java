package au.coaas.cqp.parser;

import au.coaas.base.proto.ListOfString;
import au.coaas.cqp.proto.*;
import au.coaas.cqp.util.ConditionBuilder;
import au.coaas.cqp.util.FunctionCallParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ali on 13/02/2017.
 */
public class CdqlVisitorImpl extends CdqlBaseVisitor<String> {

    private CDQLQuery.Builder query;

    private ContextFunction.Builder contextFunction;
    
    private HashMap<String, String> nameSpaces;

    private final Map<String, List<String>> errors;

    private final String regex = "1\\d{9}";

    private final  Pattern epochPattern = Pattern.compile(regex, Pattern.MULTILINE);

    public CdqlVisitorImpl() {
        nameSpaces = new HashMap<>();
        errors = new HashMap<>();
    }



    private Long getTimestamp(Object t,String datePattern) throws Exception{

        if (t == null){
            return null;
        }
        if(t instanceof Long){
            return (Long) t;
        }

        if(t instanceof Integer){
            return Long.valueOf((Integer)t);
        }

        if(t instanceof String){
            String stringDate = ((String) t).trim();
            if(datePattern != null){
                SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                return sdf.parse(stringDate).getTime();
            }else{
                final Matcher matcher = epochPattern.matcher(stringDate);
                if(matcher.matches()){
                    return Long.valueOf(stringDate);
                }
                TemporalAccessor ta = DateTimeFormatter.ISO_INSTANT.parse(stringDate);
                Instant i = Instant.from(ta);
                return Date.from(i).getTime();
            }
        }

        throw new Exception();

    }

    private void addError(String type,String err){
        if (errors.containsKey(type)) {
            errors.get(type).add(err);
        } else {
            List<String> errorList = new ArrayList<>();
            errorList.add(err);
            errors.put(type, errorList);
        }
    }

    @Override
    public String visitRule_Meta_Config(CdqlParser.Rule_Meta_ConfigContext ctx) {

        String textMeta = ctx.children.get(2).getText();
        CDQLMeta.Builder metaBuilder = CDQLMeta.newBuilder().setBody(textMeta);

        JSONObject meta = new JSONObject(textMeta);

        if(meta.has("timestamps")){
            JSONObject timestamps = meta.getJSONObject("timestamps");
            TimeWindow.Builder timeWindowBuilder = TimeWindow.newBuilder();
            String datePattern = timestamps.optString("format");
            try {
                Object start = timestamps.opt("start");
                timeWindowBuilder.setStart(this.getTimestamp(start,datePattern));
            }catch (Exception e){
                this.addError("Meta", "The Start date in timestamp cannot be parsed");
            }
            try {
                Object end = timestamps.opt("end");
                timeWindowBuilder.setEnd(this.getTimestamp(end,datePattern));
            }catch (Exception e){
                this.addError("Meta", "The End date in timestamp cannot be parsed");
            }
            metaBuilder.setTimeWindow(timeWindowBuilder.build());
        }

        query.setMeta(metaBuilder.build());

        return super.visitRule_Meta_Config(ctx);
    }

    @Override
    public String visitRule_query(CdqlParser.Rule_queryContext ctx) {
        query = CDQLQuery.newBuilder();
        query.setQueryType(QueryType.PULL_BASED);
        return super.visitRule_query(ctx);
    }

    @Override
    public String visitRule_Output_Config(CdqlParser.Rule_Output_ConfigContext ctx) {
        OutputConfig.Builder outputConfig = OutputConfig.newBuilder().setOutputStructure(OutputStructureType.valueOf(ctx.getChild(2).getText()));
        query.getConfigBuilder().setOutputConfig(outputConfig);
        return null;
    }

    @Override
    public String visitRule_Callback_Config(CdqlParser.Rule_Callback_ConfigContext ctx) {
        CDQLCallback.Builder callback = query.getCallbackBuilder();
        JSONObject jo = new JSONObject(ctx.getChild(2).getText());
        String method = jo.getString("method");
        if (method.toLowerCase().equals("fcm")) {
            callback.setCallbackMethod(CallBackMethod.FCM);
        } else if (method.toLowerCase().equals("post")) {
            callback.setCallbackMethod(CallBackMethod.HTTPPOST);
        }
        if (jo.has("body")) {
            callback.setBody(jo.getString("body"));
        }
        switch (callback.getCallbackMethod()) {
            case FCM:
                if (jo.has("fcmID")) {
                    callback.setFcmID(jo.getString("fcmID"));
                } else {
                    callback.setFcmTopic(jo.getString("fcmTopic"));
                }
                break;
            case HTTPPOST:
                callback.setHttpURL(jo.getString("url"));
                if (jo.has("headers")) {
                    JSONObject joHeaders = jo.getJSONObject("headers");
                    for (String key : joHeaders.keySet()) {
                        if (key.equalsIgnoreCase("Authorization")) {
                            if (joHeaders.get(key) instanceof JSONObject) {
                                JSONObject authObj = joHeaders.getJSONObject(key);
                                callback.setAuthorizationType(authObj.getString("type"));
                                switch (authObj.getString("type").toLowerCase()) {
                                    case "basic auth":
                                    case "basic":
                                        callback.setUserName(authObj.getString("username"));
                                        callback.setPassword(authObj.getString("password"));
                                }
                            }
                            continue;
                        }
                        callback.putHeaders(key, joHeaders.getString(key));
                    }
                }
                break;
        }
        return null;
    }

    @Override
    public String visitRuel_Push(CdqlParser.Ruel_PushContext ctx) {
        query.setQueryType(QueryType.PUSH_BASED);
        return super.visitRuel_Push(ctx);
    }

    @Override
    public String visitRule_Prefix(CdqlParser.Rule_PrefixContext ctx) {
        if (nameSpaces.containsKey(ctx.getChild(1).getText())) {
            if (errors.containsKey("Prefix")) {
                errors.get("Prefix").add("prefix " + ctx.getChild(1).getText() + "is already assigned to namespace " + nameSpaces.get(ctx.getChild(1).getText() + " in this query."));
            } else {
                List<String> perfixError = new ArrayList<>();
                perfixError.add(ctx.getChild(1).getText() + "is already assigned to namespace " + nameSpaces.get(ctx.getChild(1).getText() + " in this query."));
                errors.put("Prefix", perfixError);
            }
        }
        nameSpaces.put(ctx.getChild(1).getText(), ctx.getChild(3).getText());

        return null;
    }

    @Override
    public String visitRule_select_Attribute(CdqlParser.Rule_select_AttributeContext ctx) {
        String attribute = ctx.getText();
        Map attrs = query.getSelectBuilder().getSelectAttrsMap();

        String[] split = attribute.split("\\.", 2);
        if (attrs.containsKey(split[0])) {
            ListOfContextAttribute loca = (ListOfContextAttribute) attrs.get(split[0]);
            ListOfContextAttribute.Builder locaBuilder = loca.toBuilder().addValue(ContextAttribute.newBuilder().setAttributeName(split[0]).setAttributeName(split[1]).build());
            attrs.put(split[0], locaBuilder.build());
        } else {
            ListOfContextAttribute.Builder locaBuilder = ListOfContextAttribute.newBuilder();
            locaBuilder.addValue(ContextAttribute.newBuilder().setAttributeName(split[0]).setAttributeName(split[1]).build());
            query.getSelectBuilder().putSelectAttrs(split[0], locaBuilder.build());
        }
        return null;
    }

    @Override
    public String visitRule_select_FunctionCall(CdqlParser.Rule_select_FunctionCallContext ctx) {
        FunctionCall.Builder visitFunctionCall;
        try {
            visitFunctionCall = FunctionCallParser.visitFunctionCall((CdqlParser.Rule_FunctionCallContext) ctx.getChild(0));
            query.getSelectBuilder().addSelectFunctions(visitFunctionCall);
        } catch (UnsupportedEncodingException ex) {
            if (errors.containsKey("FunctionCall")) {
                errors.get("FunctionCall").add("FunctionCall " + ex.getMessage());
            } else {
                List<String> perfixError = new ArrayList<>();
                perfixError.add("FunctionCall " + ex.getMessage());
                errors.put("Prefix", perfixError);
            }
        }
        return null;
    }

    @Override
    public String visitRule_Start(CdqlParser.Rule_StartContext ctx) {
        if (ctx.getChild(0) instanceof CdqlParser.Rule_ConditionContext) //if the query is event-based
        {
            try {
                CDQLWhen.Builder when = CDQLWhen.newBuilder().setCondition((ConditionBuilder.buildCondition(ctx, "when")));
                query.setWhen(when);
            } catch (UnsupportedEncodingException ex) {
                if (errors.containsKey("FunctionCall")) {
                    errors.get("FunctionCall").add("FunctionCall " + ex.getMessage());
                } else {
                    List<String> perfixError = new ArrayList<>();
                    perfixError.add("FunctionCall " + ex.getMessage());
                    errors.put("Prefix", perfixError);
                }
            }
        } else if (ctx.getChild(0) instanceof CdqlParser.Rule_Date_TimeContext) //if the query is time-based - sub-type of event-based but it will be handled differently
        {
            ParseTree dateTime = ctx.getChild(0);
            try {
                String datePattern = "dd/MM/yyyy";
                String timeZone = null;
                if (dateTime.getChild(1) != null) { //time
                    datePattern += "HH:mm";
                    if (dateTime.getChild(1).getChild(3) != null) { //second or timezone or both
                        if (dateTime.getChild(1).getChild(4) != null) { //second
                            datePattern += ":ss";
                            if (dateTime.getChild(1).getChild(5) != null) //timezone
                            {
                                timeZone = dateTime.getChild(1).getChild(5).getText();
                            }
                        } else //only time zone
                        {
                            timeZone = dateTime.getChild(1).getChild(3).getText();
                        }
                    }
                }
                SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                if (timeZone != null) {
                    sdf.setTimeZone(TimeZone.getTimeZone(timeZone));
                }
                CDQLWhen when = CDQLWhen.newBuilder().setTime(sdf.parse(ctx.getText()).getTime()).build();
                query.setWhen(when);
            } catch (ParseException ex) {
                ListOfString.Builder los = ListOfString.newBuilder().addValue(ex.getMessage());
                query.putErrors("when", los.build());
            }
        }
        return visitChildren(ctx);
    }

    @Override
    public String visitRule_repeat(CdqlParser.Rule_repeatContext ctx) {
        TimeUnit tu = null;
        switch (ctx.getChild(2).getText().toLowerCase()) {
            case "ns":
                tu = TimeUnit.NANO_SECOND;
                break;
            case "ms":
                tu = TimeUnit.MILLI_SECOND;
                break;
            case "s":
                tu = TimeUnit.SECOND;
                break;
            case "m":
                tu = TimeUnit.MINUTE;
                break;
            case "h":
                tu = TimeUnit.HOUR;
                break;
            case "d":
                tu = TimeUnit.DAY;
                break;
        }
        CDQLRepeat repeat = CDQLRepeat.newBuilder().setInterval(Double.valueOf(ctx.getChild(1).getText())).setUnit(tu).build();
        query.setRepeat(repeat);
        return null;
    }

    @Override
    public String visitRule_context_entity(CdqlParser.Rule_context_entityContext ctx) {
        String[] split = ctx.getChild(3).getText().split(":");
        ContextEntityType cet = null;
        if (split.length == 2) {
            try
            {
                cet = ContextEntityType.newBuilder().setType(split[1]).setVocabURI(nameSpaces.get(split[0])).build();
            }     
            catch(Exception e)
            {
                if (errors.containsKey("Define  Entity")) {
                    errors.get("Define  Entity").add("cannot find namespace " + split[0]);
                } else {
                    List<String> perfixError = new ArrayList<>();
                    perfixError.add("cannot find namespace " + split[0]);
                    errors.put("Define  Entity", perfixError);
                }
            }
        } else {
            cet = ContextEntityType.newBuilder().setType(split[0]).build();
        }
        
        try {
            ContextEntity ce = ContextEntity.newBuilder().setCondition(ConditionBuilder.buildCondition(ctx.getChild(5), ctx.getChild(1).getText()))
                                                            .setEntityID(ctx.getChild(1).getText())
                                                            .setType(cet)
                                                            .build();
            query.getDefineBuilder().addDefinedEntities(ce);
        } catch (UnsupportedEncodingException ex) {
            if (errors.containsKey("Define  Entity")) {
                    errors.get("Define  Entity").add(ex.getMessage());
            } else {
                List<String> perfixError = new ArrayList<>();
                perfixError.add(ex.getMessage());
                errors.put("Define  Entity", perfixError);
            }
        }
                                                                
        return null;
    }

    //ToDo 
    @Override
    public String visitRule_aFunction(CdqlParser.Rule_aFunctionContext ctx) {
        contextFunction = ContextFunction.newBuilder().setType(ContextFunctionType.AGGREGATION);
        return super.visitRule_aFunction(ctx);
    }

    @Override
    public String visitRule_sFunction(CdqlParser.Rule_sFunctionContext ctx) {
        contextFunction = ContextFunction.newBuilder().setType(ContextFunctionType.SITUATION);
        contextFunction.getSFunctionBuilder().putAllNameSpaces(nameSpaces);
        return super.visitRule_sFunction(ctx);
    }

    @Override
    public String visitRule_function_id(CdqlParser.Rule_function_idContext ctx) {
        contextFunction.setFunctionTitle(ctx.getText());
        return null;
    }

    @Override
    public String visitRule_is_on_entity(CdqlParser.Rule_is_on_entityContext ctx) {
        String[] split = ctx.getChild(0).getText().split(":");
        ContextEntityType cet = null;
        if (split.length == 2) {
            cet = ContextEntityType.newBuilder().setType(split[1]).setVocabURI(nameSpaces.get(split[0])).build();
//            if (nameSpaces.getNamespaceByKey(split[0]) == null) {
//                query.addError("Define  Entity", "cannot find namespace " + split[0]);
//            }
        } else {
            cet = ContextEntityType.newBuilder().setType(split[1]).build();
        }
        String id = ctx.getChild(2).getText();
        contextFunction.getSFunctionBuilder().putRelatedEntities(id, cet);
        return null;
    }

    @Override
    public String visitRule_single_situatuin(CdqlParser.Rule_single_situatuinContext ctx) {
        String situationName = ctx.getChild(0).getText().replaceAll("\"", "");
        SituationDescription.Builder sd = SituationDescription.newBuilder();
        sd.setSituationName(situationName);
        List<WeightedAttributeDescription> weightedAttributeDescriptions = new ArrayList<>();
        for (int i = 3; i < ctx.getChildCount(); i += 2) {
            WeightedAttributeDescription wad = this.visit_situation_def((CdqlParser.Rule_situation_pairContext) ctx.getChild(i));
            if (wad == null) {
                continue;
            }
            weightedAttributeDescriptions.add(wad);
        }
        sd.addAllAttributes(weightedAttributeDescriptions);
        sd.getAttributesList().stream().forEach((attribute) -> {
            contextFunction.getSFunctionBuilder().addAllAttributes(attribute.getAttribute().getAttributeName());
        });
        contextFunction.getSFunctionBuilder().addSituations(sd);
        return null;
    }

    private WeightedAttributeDescription visit_situation_def(CdqlParser.Rule_situation_pairContext ctx) {

        String stringAttributes = ctx.getChild(0).getText().replaceAll("\\[", "").replaceAll("\\]", "");

        String[] arrayAttributes = stringAttributes.split(",");

        List<ContextAttribute> contextAttributes = new ArrayList<>();

        for (String attribute : arrayAttributes) {
            String[] arrayAttribute = attribute.split("\\.", 2);
            ContextAttribute ca;
            if (arrayAttribute.length == 2) {
                ca = ContextAttribute.newBuilder().setEntityName(arrayAttribute[0]).setAttributeName(arrayAttribute[1]).build();
            } else {
                ca = ContextAttribute.newBuilder().setEntityName(attribute).build();
            }
            contextAttributes.add(ca);
        }

        if (contextAttributes.size() > 1) {
            System.err.println("Multi attribute in CST situations is not supported");
            return null;
        }

        CdqlParser.Situation_pair_valuesContext values = (CdqlParser.Situation_pair_valuesContext) ctx.getChild(3);

        WeightedAttributeDescription.Builder result = visit_situation_pari_value(values);
        result.getAttributeBuilder().setAttributeName(stringAttributes);
        return result.build();
    }

    private WeightedAttributeDescription.Builder visit_situation_pari_value(CdqlParser.Situation_pair_valuesContext ctx) {
        WeightedAttributeDescription.Builder result = WeightedAttributeDescription.newBuilder();
        CdqlParser.Situation_weightContext weightContext;
        CdqlParser.Situation_range_valuesContext rangeContext;
        if (ctx.getChild(0) instanceof CdqlParser.Situation_weightContext) {
            weightContext = (CdqlParser.Situation_weightContext) ctx.getChild(0);
            rangeContext = (CdqlParser.Situation_range_valuesContext) ctx.getChild(2);
        } else {
            weightContext = (CdqlParser.Situation_weightContext) ctx.getChild(2);
            rangeContext = (CdqlParser.Situation_range_valuesContext) ctx.getChild(0);
        }

        String weight = weightContext.getChild(2).getText();
        result.setWeight(Integer.valueOf(weight));
        List<RegionDescription> regions = new ArrayList<>();
        for (int i = 3; i < rangeContext.getChildCount(); i += 2) {
            RegionDescription visit_Region = visit_Region((CdqlParser.Situation_pair_values_itemContext) rangeContext.getChild(i));
            regions.add(visit_Region);
        }
        AttributeDescription.Builder ad = AttributeDescription.newBuilder();
        ad.addAllRegions(regions);
        result.setAttribute(ad.build());
        return result;
    }

    private RegionDescription visit_Region(CdqlParser.Situation_pair_values_itemContext ctx) {
        RegionDescription.Builder result = RegionDescription.newBuilder();
        CdqlParser.Rule_situation_beliefContext beliefContext;
        CdqlParser.Rule_situation_valueContext valueContext;
        if (ctx.getChild(1) instanceof CdqlParser.Rule_situation_beliefContext) {
            beliefContext = (CdqlParser.Rule_situation_beliefContext) ctx.getChild(1);
            valueContext = (CdqlParser.Rule_situation_valueContext) ctx.getChild(3);
        } else {
            beliefContext = (CdqlParser.Rule_situation_beliefContext) ctx.getChild(3);
            valueContext = (CdqlParser.Rule_situation_valueContext) ctx.getChild(1);
        }
        result.setRegionBelief(Double.valueOf(beliefContext.getChild(2).getText()));

        result.setRegionValue(valueContext.getChild(2).getText());

        return result.build();
    }

    public CDQLQuery.Builder getQuery() {
        return query;
    }

    public void setQuery(CDQLQuery.Builder query) {
        this.query = query;
    }

    public ContextFunction.Builder getContextFunction() {
        return contextFunction;
    }

}
