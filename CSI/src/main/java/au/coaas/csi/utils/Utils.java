package au.coaas.csi.utils;

import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.ConQEngLog;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import com.google.common.hash.Hashing;
import okhttp3.*;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Utils {
    public static String getHashKey(Map<String,String> params){
        String hashKey = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            hashKey = hashKey + entry.getKey() + "@" + entry.getValue().replace("\"","") + ";";
        }

        return Hashing.sha256().hashString(hashKey, StandardCharsets.UTF_8).toString();
    }
}
