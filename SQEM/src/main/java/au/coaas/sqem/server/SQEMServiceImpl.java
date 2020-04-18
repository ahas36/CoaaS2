/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.sqem.server;


import au.coaas.sqem.handler.ContextEntityHandler;
import au.coaas.sqem.handler.ContextRequestHandler;
import au.coaas.sqem.handler.ContextServiceHandler;
import au.coaas.sqem.proto.SQEMServiceGrpc;

import java.util.logging.Logger;

/**
 * @author ali
 */
public class SQEMServiceImpl extends SQEMServiceGrpc.SQEMServiceImplBase {

    private static Logger log = Logger.getLogger(SQEMServiceImpl.class.getName());

    @Override
    public void handleContextRequest(au.coaas.sqem.proto.ContextRequest request,
                                     io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextRequestHandler.handle(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void registerContextEntity(au.coaas.sqem.proto.RegisterEntityRequest request,
                                      io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextEntityHandler.createEntity(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();

    }

    @Override
    public void updateContextEntity(au.coaas.sqem.proto.UpdateEntityRequest request,
                                    io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextEntityHandler.updateEntity(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void registerContextService(au.coaas.sqem.proto.RegisterContextServiceRequest request,
                                      io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextServiceHandler.register(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void updateContextServiceStatus(au.coaas.sqem.proto.UpdateContextServiceStatusRequest request,
                                           io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextServiceHandler.changeStatus(request.getId(),request.getStatus()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();

    }
}
