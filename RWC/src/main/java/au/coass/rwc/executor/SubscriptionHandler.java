package au.coass.rwc.executor;

import au.coaas.grpc.client.SQEMChannel;

import au.coaas.rwc.proto.RWCResponse;
import au.coaas.sqem.proto.EdgeStatus;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;

import au.coass.rwc.health.HealthHandler;
import au.coass.rwc.health.SystemHandler;

import java.util.Map;
import java.lang.reflect.Field;

public class SubscriptionHandler {
    private static final String master_ip = System.getenv("MASTER_IP");
    private static final String self_index  = "SELF_INDEX";

    public static void registerDevice() {
        try {
            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub =
                    SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance(master_ip).getChannel());
            SQEMResponse ackn = sqemStub.subscribeEdge(EdgeStatus.newBuilder()
                            .setSpecs(SystemHandler.getSystemStatus(false))
                    .setStatus("200").build());
            if(ackn.getStatus().equals("200")) {
                // Start sending heart beats & set self-index as env variable.
                HealthHandler.getInstance().startHeartBeat();
                updateEnv(ackn.getBody());
            }
            else {
                // Recursively attempting to establish a connection.
                Thread.sleep(1000);
                registerDevice();
            }
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void sendHeartBeats() {
        SQEMServiceGrpc.SQEMServiceFutureStub sqemStub =
                SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance(master_ip).getChannel());
        sqemStub.getHeartBeats(EdgeStatus.newBuilder()
                .setSpecs(SystemHandler.getSystemStatus(true))
                .setStatus("200").build());
    }

    private static void updateEnv(String val) {
        try {
            Map<String, String> env = System.getenv();
            Field field = env.getClass().getDeclaredField("m");
            field.setAccessible(true);
            ((Map<String, String>) field.get(env)).put(self_index, val);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static RWCResponse getMyIndex() {
        try {
            return RWCResponse.newBuilder()
                    .setBody(System.getenv(self_index))
                    .setStatus("200").build();
        } catch(Exception ex) {
            return RWCResponse.newBuilder()
                    .setStatus("500").build();
        }
    }
}
