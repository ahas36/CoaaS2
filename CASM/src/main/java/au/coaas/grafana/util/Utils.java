package au.coaas.grafana.util;

import au.coaas.base.proto.ListOfString;
import org.json.JSONArray;

public class Utils {
    public static JSONArray ListOfString2JsonArray(ListOfString los)
    {
        JSONArray result = new JSONArray();
        for(int i = 0; i < los.getValueCount();i++)
        {
            result.put(los.getValue(i));
        }
        return result;
    }
}
