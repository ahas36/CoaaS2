package au.coaas.cpree.utils;

import au.coaas.cqc.utils.HttpResponseFuture;
import au.coaas.cqc.utils.enums.HttpRequests;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.ConQEngLog;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

/**
 * @author shakthi
 */
public class ConQEngHelper {
    private static final String baseURL = "https://omega-branch-291602.ts.r.appspot.com/RRprocessor/";
    private static final String evalbaseURL = "https://omega-branch-291602.ts.r.appspot.com/QoC_CoC_Evaluator/";

    // Starts a Context Request in ConQEng.
    public static boolean createContextRequest(JSONObject cr){
        String result = call(baseURL + "context_requests",
                HttpRequests.POST, cr.toString());
        if(result != null) {
            Executors.newCachedThreadPool().submit(() -> {
                if(result.startsWith("{")){
                    JSONObject crRes = new JSONObject(result);
                    String crId = crRes.getString("_id");
                    SQEMServiceGrpc.SQEMServiceFutureStub future =
                            SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                    future.logConQEngCR(ConQEngLog.newBuilder()
                            .setId(crId).setCr(cr.toString()).setStatus(200).build());
                }
            });
            return true;
        }
        return false;
    }

    // Requests the ordered list of Context Providers based on Cost and Quality from ConQEng.
    public static boolean verifyCPOrder(JSONObject cpRequest){
        String result = call(baseURL + "context_providers",
                HttpRequests.POST, cpRequest.toString());
        if(result != null) {
            JSONArray sorted = new JSONArray(result);
            String firstCP = sorted.getString(0);
            if(firstCP != cpRequest.getString("pid")) return false;
        }
        return true;
    }

    // Reports back the performance of the last context retrieval based on selection to ConQEng.
    public static void reportPerformance(JSONObject perfRequest){
        call(evalbaseURL + "context_responses", HttpRequests.POST, perfRequest.toString());
    }

    // Reports back failed context providers to ConQEng.
    public static void reportFeedback(JSONObject perfRequest){
        call(evalbaseURL + "feedback", HttpRequests.POST, perfRequest.toString());
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
            else {
                SQEMServiceGrpc.SQEMServiceFutureStub future =
                        SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                future.logConQEngCR(ConQEngLog.newBuilder()
                                .setStatus(response.code()).setCr(body)
                                .setMessage(response.body().string().trim()).build());
            }
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
