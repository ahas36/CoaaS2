/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cqc.server;

import au.coaas.cqc.executor.*;
import au.coaas.cqc.proto.*;

import java.util.logging.Logger;

/**
 *
 * @author ali & shakthi
 */
public class CQCServiceImpl extends CQCServiceGrpc.CQCServiceImplBase {

    private static Logger log = Logger.getLogger(CQCServiceImpl.class.getName());

    @Override
    public void execute(au.coaas.cqc.proto.ExecutionRequest request,
            io.grpc.stub.StreamObserver<CdqlResponse> responseObserver){
        try {
            responseObserver.onNext(CDQLExecutor.execute(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void unsubscribe(au.coaas.cqc.proto.ExecutionRequest request,
                        io.grpc.stub.StreamObserver<CdqlResponse> responseObserver){
        try {
            // Unsubscribe from a push query
            responseObserver.onNext(CDQLExecutor.unsubscribe(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void registerContextEntity(au.coaas.cqc.proto.ExecutionRequest request,
                                      io.grpc.stub.StreamObserver<CdqlResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextEntityManager.registerContextEntity(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();

    }

    @Override
    public void updateContextEntity(au.coaas.cqc.proto.ExecutionRequest request,
                                    io.grpc.stub.StreamObserver<CdqlResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextEntityManager.updateContextEntity(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void registerContextService(au.coaas.cqc.proto.ExecutionRequest request,
                                    io.grpc.stub.StreamObserver<CdqlResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextServiceManager.registerContextService(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void registerContextConsumer(au.coaas.cqc.proto.ExecutionRequest request,
                                       io.grpc.stub.StreamObserver<CdqlResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextConsumerManager.registerContextConsumer(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void changeRegisterState(au.coaas.cqc.proto.RegisterState request,
                                    io.grpc.stub.StreamObserver<Empty> responseObserver) {
        try {
            responseObserver.onNext(PullBasedExecutor.updateRegistryState(request.getState()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void handleSituation(au.coaas.cqc.proto.EventRequest request,
                                      io.grpc.stub.StreamObserver<CdqlResponse> responseObserver) {
        try {
            responseObserver.onNext(SituationManager.handleEvent(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getEventStats(au.coaas.cqc.proto.Empty request,
                              io.grpc.stub.StreamObserver<EventStats> responseObserver) {
        try {
            responseObserver.onNext(SituationManager.getEventHandlingStats());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void resetEventStats(au.coaas.cqc.proto.Empty request,
                              io.grpc.stub.StreamObserver<Empty> responseObserver) {
        try {
            responseObserver.onNext(SituationManager.restartMonitoring());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void cancelSubscription (au.coaas.cqc.proto.Subscription request,
                                io.grpc.stub.StreamObserver<RegisterState> responseObserver) {
        try {
            responseObserver.onNext(PushBasedExecutor.cancelJob(request.getId()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void removeMonitor (au.coaas.cqc.proto.CPMonitor request,
                                io.grpc.stub.StreamObserver<au.coaas.cqc.proto.Empty> responseObserver){
        try {
            RetrievalManager.removeMonitored(request.getContextID(),request.getContextEntity());
            responseObserver.onNext(au.coaas.cqc.proto.Empty.newBuilder().build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void modifyCPMonitor(au.coaas.cqc.proto.CPMonitor request,
                                io.grpc.stub.StreamObserver<au.coaas.cqc.proto.Empty> responseObserver){
        try {
            RetrievalManager.updateMonitored(request.getContextID(),
                    request.getContextEntity());
            responseObserver.onNext(au.coaas.cqc.proto.Empty.newBuilder().build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void refreshPredictedPath(au.coaas.cqc.proto.PathRequest request,
                                io.grpc.stub.StreamObserver<au.coaas.cqc.proto.Path> responseObserver){
        try {
            responseObserver.onNext(SituationManager.processPredictedPath(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }
}
