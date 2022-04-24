package au.coaas.sqem.monitor;

import au.coaas.sqem.proto.SQEMResponse;

public class RoutineManager {
    public static SQEMResponse evict() {
        return SQEMResponse.newBuilder().setStatus("200").build();
    }

    public static SQEMResponse refresh() {
        return SQEMResponse.newBuilder().setStatus("200").build();
    }

    public static SQEMResponse learn() {
        return SQEMResponse.newBuilder().setStatus("200").build();
    }
}
