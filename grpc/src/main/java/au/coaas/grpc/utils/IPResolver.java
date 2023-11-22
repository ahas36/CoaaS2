package au.coaas.grpc.utils;

import au.coaas.grpc.client.Config;
import java.util.AbstractMap;

public class IPResolver {
    // TODO:
    // Complex dynamically allocated resource lookup should be implemented.
    public static AbstractMap.SimpleEntry convertIpToLocation (long index, Services service) {
        String ipAddress = index == Config.MY_INDEX ? "0.0.0.0" : Config.ipList.get(index);
        return new AbstractMap.SimpleEntry(ipAddress, service.port);
    }
}
