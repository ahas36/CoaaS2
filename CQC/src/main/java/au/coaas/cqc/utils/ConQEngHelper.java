package au.coaas.cqc.utils;

import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import au.coaas.cqc.utils.enums.HttpRequests;

import java.util.concurrent.ExecutionException;


public class ConQEngHelper {
    private static final String baseURL = "https://omega-branch-291602.ts.r.appspot.com/RRprocessor/";
    private static final String evalbaseURL = "https://omega-branch-291602.ts.r.appspot.com/QoC_CoC_Evaluator/";

    // Starts a Context Request in ConQEng.
    public static boolean createContextRequest(JSONObject cr){
        String result = call(baseURL + "context_requests",
                HttpRequests.POST, cr.toString());
        if(result != null) return true;
        return false;
    }

    // Requests the ordered list of Context Providers based on Cost and Quality from ConQEng.
    public static List<JSONObject> getCPOrder(JSONObject cpRequest, JSONArray contextServices){
        String result = call(baseURL + "context_providers",
                HttpRequests.POST, cpRequest.toString());
        List<JSONObject> orderdCPs = new ArrayList<>();
        if(result != null) {
            JSONArray sorted = new JSONArray(result);
            for(Object cpid : sorted){
                for(int i = 0; i < contextServices.length(); i++){
                    JSONObject cp = contextServices.getJSONObject(i);
                    if(cp.getJSONObject("_id").getString("$oid").equals((String)cpid)){
                        orderdCPs.add(cp);
                        contextServices.remove(i);
                        continue;
                    }
                }
            }
        }
        return orderdCPs;
    }

    // Reports back the performance of the last context retrieval based on selection to ConQEng.
    public static void reportPerformance(JSONObject perfRequest){
        call(evalbaseURL + "context_responses", HttpRequests.POST, perfRequest.toString());
    }

    private static String call(String serviceUrl, HttpRequests type, String body){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10, TimeUnit.SECONDS);
        builder.writeTimeout(10, TimeUnit.SECONDS);
        builder.readTimeout(10, TimeUnit.SECONDS);
        OkHttpClient client = builder.build();

        HttpResponseFuture fu_res = new HttpResponseFuture();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Request.Builder request = new Request.Builder()
                .url(serviceUrl);
        switch (type){
            case GET:
                request.get();
                break;
            case POST:
                RequestBody formBody = RequestBody.create(JSON, body);
                request.post(formBody);
                break;
        }

        try{
            Call call = client.newCall(request.build());
            call.enqueue(fu_res);
            Response response = fu_res.future.get();
            if(response.isSuccessful())
                return response.body().string().trim();
        }
        catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
            JSONObject error = new JSONObject();
            error.put("message", e.getMessage());
            StringWriter strOut = new StringWriter();
            PrintWriter writer = new PrintWriter(strOut);
            e.printStackTrace(writer);
            writer.flush();
            error.put("stack trace", strOut.toString());
        }

        return null;
    }
}
