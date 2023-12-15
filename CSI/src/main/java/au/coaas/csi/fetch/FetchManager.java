package au.coaas.csi.fetch;

import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSSummary;
import au.coaas.csi.proto.ContextServiceInvokerRequest;

import au.coaas.csi.utils.HttpResponseFuture;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.*;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ali & shakthi
 */
public class FetchManager {

    public static CSIResponse fetch(ContextServiceInvokerRequest contextService) {
        JSONObject cs = new JSONObject(contextService.getContextService().getJson());

        JSONObject info = cs.getJSONObject("info");
        JSONObject sla = cs.getJSONObject("sla");

        String serviceUrl = info.getString("serviceURL");
        if(info.get("params").getClass() == JSONArray.class){
            JSONArray definedParamSet = info.getJSONArray("params");
            Map<String, String> setParams = contextService.getParamsMap();

            if(serviceUrl.contains("{0}")){
                for(Object pr : definedParamSet){
                    String key = ((JSONObject) pr).getString("key");
                    String defaultVal = ((JSONObject) pr).get("value").toString();
                    String placeholder = ((JSONObject) pr).getString("name");

                    if(setParams.containsKey(key)){
                        String paramVal = setParams.get(key);
                        if(paramVal.startsWith("{")){
                            JSONObject value = new JSONObject(paramVal);
                            Object readVal = value.get("value");
                            String setVal = readVal instanceof String ? (String)readVal : String.valueOf(readVal);
                            serviceUrl = serviceUrl.replace(placeholder, setVal);
                        }
                        else
                            serviceUrl = serviceUrl.replace(placeholder, paramVal.replaceAll("\"",""));
                    }
                    else
                        serviceUrl = serviceUrl.replace(placeholder, defaultVal.replaceAll("\"",""));
                }
            }
            else {
                for (Map.Entry<String, String> entry : setParams.entrySet()) {
                    if(serviceUrl.endsWith("{}"))
                        serviceUrl = serviceUrl.replaceAll("\\{\\}", entry.getValue().replaceAll("\"",""));
                    else
                        serviceUrl = serviceUrl.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue().replaceAll("\"",""));
                }
            }
        }

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();
                
        HttpResponseFuture fu_res = new HttpResponseFuture();

        Request request = new Request.Builder()
                .url(serviceUrl)
                .get()
                .build();

        try {
            // Response response = client.newCall(request).execute();
            Call call = client.newCall(request);
            call.enqueue(fu_res);
            Response response = fu_res.future.get();

            if(response.code() != 200){
                response.close();
                return CSIResponse.newBuilder().setStatus("500").build();
            }

            // This is what I should be caching at this point. Not the entity.
            // When the context service is resolved, the cache should look if the context service response is available in cache.
            String res = response.body().string().trim();

            if(cs.has("attributes")){
                JSONObject result = semanticMapper(cs.getJSONArray("attributes"), res, info.optString("resultTag",null));
                // This return value should be what should be cached.
                CSSummary.Builder summary = CSSummary.newBuilder()
                        .setId(cs.getJSONObject("_id").getString("$oid"))
                        .setFreshness(sla.getJSONObject("freshness").getLong("value"))
                        .setPrice(sla.getJSONObject("cost").getDouble("value"));
                if(sla.getJSONObject("freshness").has("fthresh"))
                    summary.setFthresh(sla.getJSONObject("freshness").getDouble("fthresh"));

                return CSIResponse.newBuilder().setStatus("200")
                        .setBody(result.toString())
                        .setSummary(summary)
                        .build();
            }

            JSONObject rawResult = null;
            if(res.startsWith("{")){
                rawResult = new JSONObject(res);
            }else if (res.startsWith("[")){
                Double avgAge = 0.0;
                rawResult = new JSONObject();
                JSONArray results = new JSONArray(res);
                if(results.getJSONObject(0).has("age")){
                    Double sum = 0.0;
                    for(int i = 0; i<results.length(); i++){
                        sum += results.getJSONObject(i)
                                .getJSONObject("age").getDouble("value");
                    }
                    avgAge = (sum*1000) / results.length();
                }
                rawResult.put("results", results);
                rawResult.put("avgAge", avgAge);
            }else {
                rawResult = new JSONObject();
                rawResult.put("results", res);
                rawResult.put("avgAge", 0.0); // avgAge in miliseconds
            }

            return CSIResponse.newBuilder().setStatus("200")
                    .setBody(rawResult.toString())
                    .setSummary(CSSummary.newBuilder()
                            .setId(cs.getString("_id"))
                            .setFreshness(sla.getJSONObject("freshness").getLong("value"))
                            .setPrice(sla.getJSONObject("cost").getDouble("value")).build())
                    .build();
            // Or this raw result.
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("Message", e.getMessage());
            StringWriter strOut = new StringWriter();
            PrintWriter writer = new PrintWriter(strOut);
            e.printStackTrace(writer);
            writer.flush();
            error.put("Stack trace", strOut.toString());
            return CSIResponse.newBuilder().setStatus("500").setBody(error.toString()).build();
        }
    }

    private static Object getAttributeValue(String res, String valueIndex) {
        String[] valuesIndexArray = valueIndex.split("\\.");
        Object value = new JSONObject(res);
        Object response = null;

        // The original code seems to be having a limitation here. It assumes the passed parameter key is available
        // in the 1st level of the response body itself. Nested properties are ignored.
        for (int j = 0; j < valuesIndexArray.length; j++) {
            if (value instanceof JSONObject) {
                // See if the key is in the 1st level
                if(((JSONObject) value).has(valuesIndexArray[j])){
                    Object attaVal = ((JSONObject) value).get(valuesIndexArray[j]);
                    if(attaVal.toString().startsWith("{")){
                        JSONObject resOb = new JSONObject(attaVal.toString());
                        if(resOb.has("value")) response = resOb.get("value");
                        else if(resOb.has("price")) response = resOb.get("price");
                        else response = attaVal;
                    }
                    else response = attaVal;
                }
                else {
                    Set<String> keys = ((JSONObject) value).keySet();
                    for(String key : keys){
                        if(((JSONObject) value).get(key) instanceof JSONObject){
                            Object attaVal = getAttributeValue(((JSONObject) value).get(key).toString(), valuesIndexArray[j]);
                            if(attaVal != null){
                                if(attaVal.toString().startsWith("{")){
                                    JSONObject resOb = new JSONObject(attaVal.toString());
                                    if(resOb.has("value")) response = resOb.get("value");
                                    else if(resOb.has("price")) response = resOb.get("price");
                                    else response = attaVal;
                                    break;
                                }
                                response = attaVal;
                                break;
                            }
                        }
                    }
                }
            } else if (value instanceof JSONArray) {
                response = ((JSONArray) value).get(Integer.valueOf(valuesIndexArray[j]));
            }
        }

        return response;
    }

    final static Pattern pattern = Pattern.compile("([a-z]|[A-Z])+(\\d|-|_|([a-z]|[A-Z]))*(\\.(\\d|([a-z]|[A-Z])|-|_)+)*", Pattern.MULTILINE);

    private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    private static Object evaluateExpression(String expression){
        try {
            if(expression != null && (expression.startsWith("{") || expression.startsWith("["))){
                JsonParser parse = new JsonParser();
                return parse.parse(expression);
            }
            return expression;
        } catch (Exception e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error",e.getMessage());
            return jsonObject;
        }
    }

    private static Object getResponseValue(String res, String expression) {
        // e.g., res = response object, expression = name of the attribute in the response
        if(expression.startsWith("&"))
            expression = expression.replaceAll("&","");

        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String pram = matcher.group(0).trim();
            Object attributeValue = getAttributeValue(res,pram);
            expression = attributeValue != null ? expression.replaceAll(pram,
                        attributeValue.toString()): null;
        }
        // return expression;
        return evaluateExpression(expression);
    }

    private static JSONArray parseCoordintes(JSONArray ja,String res)
    {
        JSONArray result = new JSONArray();
        for (int i = 0; i<ja.length();i++)
        {
            Object item = ja.get(i);
            if(item instanceof JSONArray)
            {
                result.put(parseCoordintes((JSONArray) item,res));
            }else {
                result.put(Double.valueOf(getResponseValue(res,item.toString()).toString()));
            }
        }
        return result;
    }

    private static JSONObject mapJsonObject(JSONArray attributes, String service)
            // params: attributes list, response
    {
        try {
            JSONObject result = new JSONObject();
            for (int i = 0; i < attributes.length(); i++) {
                JSONObject attribute = attributes.getJSONObject(i);
                Object value;
                if (attribute.has("attrs")) {
                    value = mapJsonObject(attribute.getJSONArray("attrs"), service);
                    if(attribute.has("GeoJson") && attribute.getBoolean("GeoJson")){
                        ((JSONObject) value).put("type",attribute.optString("type","Point"));
                        JSONArray coordinates = new JSONArray();
                        coordinates.put(((JSONObject) value).optDouble("longitude"));
                        coordinates.put(((JSONObject) value).optDouble("latitude"));
                        ((JSONObject) value).put("coordinates",coordinates);
                    }
                } else {
                    value = getResponseValue(service, attribute.getString("value"));
                    if(attribute.getJSONObject("key").getString("label").equals("location")
                            && value instanceof JsonObject){
                        ((JsonObject) value).addProperty("type",
                                attribute.optString("type","Point"));
                        JsonArray coordinates = new JsonArray();
                        coordinates.add(((JsonObject) value).get("longitude").getAsNumber());
                        coordinates.add(((JsonObject) value).get("latitude").getAsNumber());
                        ((JsonObject) value).add("coordinates",coordinates);
                    }
                }
                result.put(attribute.getJSONObject("key").getString("label"), value);
            }

            JSONObject response = new JSONObject(service);
            if(response.has("age")){
                result.put("age",response.getJSONObject("age"));
            }
            else if (response.has("observedTime")){
                long ageInMilis = System.currentTimeMillis() - response.getLong("observedTime");
                JSONObject ageObj = new JSONObject();
                ageObj.put("value", ageInMilis/1000.0);
                ageObj.put("unitText", "s");
            }

            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    private static JSONObject semanticMapper(JSONArray attributes, String service, String resultTag) {
        if(service.trim().startsWith("[") || resultTag!=null)
        {
            JSONArray result = new JSONArray();

            JSONArray items;
            if(service.trim().startsWith("[")){
                items = new JSONArray(service);
            }else{
                items = new JSONObject(service).getJSONArray(resultTag);
            }

            int numberOfThreads = 10;

            int numberOfItemsPerTask = 100;

            int numberOfIterations = (int)(items.length()/numberOfItemsPerTask) + 1;

            ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
            Stack<Double> ages = new Stack<>();

            for (int factor = 0; factor< numberOfIterations ; factor++)
            {
                int finalFactor = factor;
                executorService.submit(() -> {
                    int start = numberOfItemsPerTask * finalFactor;
                    int end =  Math.min(items.length(),start+numberOfItemsPerTask);
                    for(int i = start; i < end; i++){
                        JSONObject entity = mapJsonObject(attributes,items.getJSONObject(i).toString());
                        ages.push(entity.getJSONObject("age").getDouble("value"));
                        result.put(entity);
                    }
                });
            }

            executorService.shutdown();

            try {
                executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {

            }

            JSONObject finalResult = new JSONObject();
            finalResult.put("results", result);

            List<Double> ageArrary = new ArrayList<>();
            while(!ages.isEmpty()){
                ageArrary.add(ages.pop());
            }

            Double sum = 0.0;
            for(int i=0; i < ageArrary.size() ; i++)
                sum = sum + ageArrary.get(i);
            finalResult.put("avgAge", ageArrary.size()>0?(sum*1000.0)/ageArrary.size():0.0);
            return finalResult;
        } else {
            return mapJsonObject(attributes,service);
        }
    }

    public static CSIResponse mappingService (String jsonResponse, String attributes) {
        JSONObject csAttributes = new JSONObject(attributes);
        JSONObject result = semanticMapper(csAttributes.getJSONArray("attributes"), jsonResponse, null);
        return CSIResponse.newBuilder().setStatus("200")
                .setBody(result.toString())
                .build();
    }
}

