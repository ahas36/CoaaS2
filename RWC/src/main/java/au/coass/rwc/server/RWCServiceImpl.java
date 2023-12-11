package au.coass.rwc.server;

import au.coaas.rwc.proto.Empty;
import au.coaas.rwc.proto.MigrationRequest;
import au.coaas.rwc.proto.RWCResponse;
import au.coaas.rwc.proto.RWCServiceGrpc;
import au.coass.rwc.executor.MigrationHandler;
import au.coass.rwc.executor.SubscriptionHandler;

import java.util.logging.Logger;

/**
 * @author shakthi
 */
public class RWCServiceImpl extends RWCServiceGrpc.RWCServiceImplBase{
    private static Logger log = Logger.getLogger(RWCServiceImpl.class.getName());

    @Override
    public void heartBeat (Empty request, io.grpc.stub.StreamObserver<RWCResponse> responseObserver){
        try {
            responseObserver.onNext(RWCResponse.newBuilder().setStatus("200").build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getNodeIndex (Empty request, io.grpc.stub.StreamObserver<RWCResponse> responseObserver){
        try {
            responseObserver.onNext(SubscriptionHandler.getMyIndex());
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void hotMigrate (MigrationRequest request, io.grpc.stub.StreamObserver<RWCResponse> responseObserver){
        try {
            responseObserver.onNext(MigrationHandler.migrate(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }
}
