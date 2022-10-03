/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cqc.server;

import au.coaas.cqc.executor.*;
import au.coaas.cqc.proto.CQCServiceGrpc;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.Empty;

import java.util.logging.Logger;

/**
 *
 * @author ali
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
}
