/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cqc.server;

import au.coaas.cqc.executor.CDQLExecutor;
import au.coaas.cqc.executor.ContextEntityManager;
import au.coaas.cqc.executor.ContextServiceManager;
import au.coaas.cqc.executor.PullBasedExecutor;
import au.coaas.cqc.proto.CQCServiceGrpc;
import au.coaas.cqc.proto.CdqlResponse;


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
            CdqlResponse response = CDQLExecutor.execute(request.getCdql(),request.getPage(),request.getLimit());
            responseObserver.onNext(response);
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
}
