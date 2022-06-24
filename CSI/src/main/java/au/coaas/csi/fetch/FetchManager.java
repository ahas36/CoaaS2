package au.coaas.csi.fetch;

import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSSummary;
import au.coaas.csi.proto.ContextServiceInvokerRequest;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import javax.script.ScriptEngineManager;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class FetchManager {

    public static CSIResponse fetch(ContextServiceInvokerRequest contextService) {
        JSONObject cs = new JSONObject(contextService.getContextService().getJson());

        JSONObject info = cs.getJSONObject("info");
        JSONObject sla = cs.getJSONObject("sla");

        String serviceUrl = info.getString("serviceURL");

        // How does this definition fit all?
        for (Map.Entry<String, String> entry : contextService.getParamsMap().entrySet()) {
            if(serviceUrl.endsWith("{}"))
                serviceUrl = serviceUrl.replaceAll("\\{\\}", entry.getValue().replaceAll("\"",""));
            else
                serviceUrl = serviceUrl.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue().replaceAll("\"",""));
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(serviceUrl)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            // This is what I should be caching at this point. Not the entity.
            // When the context service is resolved, the cache should look if the context service response is available in cache.
            String res = response.body().string().trim();

            if(cs.has("attributes")){
                Object result = semanticMapper(cs.getJSONArray("attributes"), res, info.optString("resultTag",null));
                // This return value should be what should be cached.
                return CSIResponse.newBuilder().setStatus("200")
                        .setBody(result.toString())
                        .setSummary(CSSummary.newBuilder()
                                .setId(cs.getJSONObject("_id").getString("$oid"))
                                .setFreshness(sla.getJSONObject("freshness").getLong("value"))
                                .setFthresh(sla.getJSONObject("freshness").getDouble("fthresh"))
                                .setPrice(sla.getJSONObject("cost").getDouble("value")).build())
                        .build();
            }

            JSONObject rawResult = null;
            if(res.startsWith("{")){
                rawResult = new JSONObject(res);
            }else if (res.startsWith("[")){
                rawResult = new JSONObject();
                rawResult.put("result", new JSONArray(res));
            }else {
                rawResult = new JSONObject();
                rawResult.put("result", res);
            }

            return CSIResponse.newBuilder().setStatus("200")
                    .setBody(rawResult.toString())
                    .setSummary(CSSummary.newBuilder()
                            .setId(cs.getString("_id"))
                            .setFreshness(sla.getJSONObject("freshness").getLong("value"))
                            .setPrice(sla.getJSONObject("cost").getDouble("value")).build())
                    .build();
            // Or this raw result.
        } catch (IOException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("message", e.getMessage());
            StringWriter strOut = new StringWriter();
            PrintWriter writer = new PrintWriter(strOut);
            e.printStackTrace(writer);
            writer.flush();
            error.put("stack trace", strOut.toString());
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
                if(((JSONObject) value).has(valuesIndexArray[j]))
                    response = ((JSONObject) value).get(valuesIndexArray[j]);
                else {
                    Set<String> keys = ((JSONObject) value).keySet();
                    for(String key : keys){
                        if(((JSONObject) value).get(key) instanceof JSONObject){
                            Object attaVal = getAttributeValue(((JSONObject) value).get(key).toString(), valuesIndexArray[j]);
                            if(attaVal != null){
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

    private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("rhino");

    private static Object evaluateExpression(String expression){
        try {
            ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
            return engine.eval(expression);
        } catch (ScriptException e) {
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
            if(attributeValue instanceof String){
                expression = expression.replaceAll(pram,"\""+attributeValue.toString()+"\"");
            }
            else {
                expression = expression.replaceAll(pram,attributeValue.toString());
            }
        }
        return expression;
        // return evaluateExpression(expression);
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
                }
                result.put(attribute.getJSONObject("key").getString("label"), value);
            }
            return result;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return new JSONObject();
    }


    private static Object semanticMapper(JSONArray attributes, String service, String resultTag) {
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

            for (int factor = 0; factor< numberOfIterations ; factor++)
            {
                int finalFactor = factor;
                executorService.submit(() -> {
                    int start = numberOfItemsPerTask * finalFactor;
                    int end =  Math.min(items.length(),start+numberOfItemsPerTask);
                    for(int i = start; i < end; i++){
                        result.put(mapJsonObject(attributes,items.getJSONObject(i).toString()));
                    }
                });
            }

            executorService.shutdown();

            try {
                executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {

            }
            JSONObject finalResult = new JSONObject();
            finalResult.put("results",result);
            return finalResult;
        }else {
            return mapJsonObject(attributes,service);
        }
    }

}
