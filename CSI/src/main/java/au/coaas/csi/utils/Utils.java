package au.coaas.csi.utils;

import com.google.common.hash.Hashing;
import com.google.protobuf.Message;
import com.google.protobuf.Struct;
import com.google.protobuf.util.JsonFormat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author shakthi
 */
public class Utils {
    public static String getHashKey(Map<String,String> params){
        String hashKey = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            hashKey = hashKey + entry.getKey() + "@" + entry.getValue().replace("\"","") + ";";
        }

        return Hashing.sha256().hashString(hashKey, StandardCharsets.UTF_8).toString();
    }

    public static Message fromJson(String json, Message.Builder structBuilder) throws IOException {
        JsonFormat.parser().ignoringUnknownFields().merge(json, structBuilder);
        return structBuilder.build();
    }
}
