package au.coaas.grpc.client;

import java.util.HashMap;

public class Config {
    public static int MAX_MESSAGE_SIZE = 50 * 1024 * 1024;
    // TODO:
    // MY_INDEX should be dynamically set at the subscription time.
    public static long MY_INDEX = 1234567890;
    // TODO:
    // ipList should also be received from the controller at the initiation time.
    // Current only a single known entry is set for simple testing.
    public static HashMap<Long,String> ipList = new HashMap () {{
        put(Long.parseLong("987654321"), "192.240.24.24");
    }};
}
