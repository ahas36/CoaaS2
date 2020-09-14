package au.coaas.csi.fetch;

import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;
import netscape.javascript.JSObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FetchManager {

    public static CSIResponse fetch(ContextServiceInvokerRequest contextService) {
        JSONObject cs = new JSONObject(contextService.getContextService().getJson());

        JSONObject info = cs.getJSONObject("info");

        String serviceUrl = info.getString("serviceURL");

        for (Map.Entry<String, String> entry : contextService.getParamsMap().entrySet()) {
            serviceUrl = serviceUrl.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue().replaceAll("\"",""));
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(serviceUrl)
                .get()
                .build();

        try {
            Response response = client.newCall(request).execute();
            String res = response.body().string().trim();
            if(cs.has("attributes")){
                Object result = semanticMapper(cs.getJSONArray("attributes"), res, info.optString("resultTag",null));
                return CSIResponse.newBuilder().setStatus("200").setBody(result.toString()).build();
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


            return CSIResponse.newBuilder().setStatus("200").setBody(rawResult.toString()).build();
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

        for (int j = 0; j < valuesIndexArray.length; j++) {
            if (value instanceof JSONObject) {
                value = ((JSONObject) value).get(valuesIndexArray[j]);
            } else if (value instanceof JSONArray) {
                value = ((JSONArray) value).get(Integer.valueOf(valuesIndexArray[j]));
            }
        }

        return value;
    }

    final static Pattern pattern = Pattern.compile("\\$([a-z]|[A-Z])+(\\d|-|_|([a-z]|[A-Z]))*(\\.(\\d|([a-z]|[A-Z])|-|_)+)*", Pattern.MULTILINE);

    private static ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    private static Object evaluateExpression(String expression){
        try {
            return engine.eval(expression);
        } catch (ScriptException e) {
            e.printStackTrace();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("error",e.getMessage());
            return jsonObject;
        }
    }

    private static Object getResponseValue(String res, String expression) {
        Matcher matcher = pattern.matcher(expression);
        while (matcher.find()) {
            String pram = matcher.group(0).trim().substring(1);
            Object attributeValue = getAttributeValue(res,pram);
            if(attributeValue instanceof String){
                expression = expression.replaceAll("\\$"+pram,"\""+attributeValue.toString()+"\"");
            }
            else {
                expression = expression.replaceAll("\\$"+pram,attributeValue.toString());
            }
        }
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


    private static Object semanticMapper(JSONArray attributes, String service,String resultTag) {
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
