package au.coaas.sqem.util;

import okhttp3.*;

import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.concurrent.TimeUnit;
import au.coaas.sqem.util.enums.HttpRequests;
import java.util.concurrent.ExecutionException;

public class HttpClient {
    public static String call(String serviceUrl, HttpRequests type, String body){
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
            case PUT:
                RequestBody jsonbody = RequestBody.create(JSON, body);
                request.put(jsonbody);
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
