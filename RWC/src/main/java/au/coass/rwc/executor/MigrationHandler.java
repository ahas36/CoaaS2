package au.coass.rwc.executor;

import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextService;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.rwc.proto.MigrationRequest;
import au.coaas.rwc.proto.RWCResponse;
import au.coaas.sqem.proto.*;
import io.grpc.stub.StreamObserver;

import java.util.Iterator;

public class MigrationHandler {
    private static final String master_ip = System.getenv("MASTER_IP");

    public static RWCResponse migrate(MigrationRequest request) {
        // Step 1. Migrate the last context about the entity in interest to the destination.
        SQEMServiceGrpc.SQEMServiceBlockingStub mastersqem_Stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance(master_ip).getChannel());
        SQEMResponse sqemResponse = mastersqem_Stub.updateContextEntity(request.getEnt());

        if(sqemResponse.getStatus().equals("200")) {
            // Step 2. Start the scheduler service in the destination.
            CSIServiceGrpc.CSIServiceFutureStub destination_csiStub
                    = CSIServiceGrpc.newFutureStub(CSIChannel.getInstance(
                    request.getDestination().getIpAddress()).getChannel());
            destination_csiStub.createFetchJob(ContextService.newBuilder()
                    .setMongoID(request.getProviderId())
                    .setJson(request.getCs()).setTimes(-1)
                    .setCpIndex(request.getCpIndex()).build());

            // Step 3. Stop the current scheduler from running locally.
            CSIServiceGrpc.CSIServiceFutureStub local_csiStub
                    = CSIServiceGrpc.newFutureStub(CSIChannel.getInstance().getChannel());
            local_csiStub.cancelFetchJob(ContextService.newBuilder()
                    .setMongoID(request.getProviderId())
                    .setJobParamHash(request.getJobId()).build());

            // Step 4. Change the subscription at the Master.
            mastersqem_Stub.changeSubscription(CPEdgeDevice.newBuilder()
                    .setPersistContext(true)
                    .setCpIndex(request.getCpIndex())
                    .setCpId(request.getProviderId())
                    .setEdgeDevice(request.getDestination()).build());

            // Step 5. Persist the historical context about this entity in the master.
            // Get the context recieved from the provider in the local storage as a stream.
            SQEMServiceGrpc.SQEMServiceBlockingStub localsqem_Stub
                    = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            Iterator<Chunk> all_context = localsqem_Stub.getContextFromProvider(ContextServiceId.newBuilder()
                    .setId(request.getProviderId())
                    .setEntType(request.getEnt().getEt().getType()).build());

            // Pass that stream for persistent storage to the master.
            SQEMServiceGrpc.SQEMServiceStub masterStreamStub
                    = SQEMServiceGrpc.newStub(SQEMChannel.getInstance(master_ip).getChannel());
            StreamObserver<Empty> responseObserver = new StreamObserver<Empty>() {
                @Override
                public void onNext(Empty emptyRes) {}
                @Override
                public void onError(Throwable throwable) {}
                @Override
                public void onCompleted() {}
            };

            StreamObserver<Chunk> requestObserver = masterStreamStub.persistInMaster(responseObserver);
            try {
                while (all_context.hasNext()) {
                    requestObserver.onNext(all_context.next());
                }
            } catch (RuntimeException e) {
                requestObserver.onError(e);
                throw e;
            }
            requestObserver.onCompleted();

            // Remove the locally stored context for space efficiency.
            localsqem_Stub.deleteTemporaryContext(ContextServiceId.newBuilder()
                    .setId(request.getProviderId())
                    .setEntType(request.getEnt().getEt().getType()).build());

            return RWCResponse.newBuilder().setStatus("200").build();
        }
        else {
            return RWCResponse.newBuilder().setStatus("500").build();
        }
    }
}
