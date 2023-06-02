package au.coaas.csi.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class Utils {
    public static String getHashKey(Map<String,String> params){
        String hashKey = "";
        for (Map.Entry<String, String> entry : params.entrySet()) {
            hashKey = hashKey + entry.getKey() + "@" + entry.getValue().replace("\"","") + ";";
        }

        return Hashing.sha256().hashString(hashKey, StandardCharsets.UTF_8).toString();
    }
}
