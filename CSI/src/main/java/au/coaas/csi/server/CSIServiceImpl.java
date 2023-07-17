/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.csi.server;


import au.coaas.csi.fetch.FetchManager;
import au.coaas.csi.fetch.JobSchedulerManager;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;

import java.util.logging.Logger;


/**
 * @author ali
 */
public class CSIServiceImpl extends CSIServiceGrpc.CSIServiceImplBase {

    private static Logger log = Logger.getLogger(CSIServiceImpl.class.getName());

    @Override
    public void fetch(au.coaas.csi.proto.ContextServiceInvokerRequest request,
                      io.grpc.stub.StreamObserver<CSIResponse> responseObserver) {
        try {
            CSIResponse response = FetchManager.fetch(request);
            responseObserver.onNext(response);
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }


    @Override
    public void createFetchJob(au.coaas.csi.proto.ContextService request,
                               io.grpc.stub.StreamObserver<CSIResponse> responseObserver) {
        try {
            CSIResponse response = JobSchedulerManager.getInstance().registerJob(request);
            responseObserver.onNext(response);
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();

    }

    @Override
    public void updateFetchJob(au.coaas.csi.proto.ContextService request,
                               io.grpc.stub.StreamObserver<CSIResponse> responseObserver) {
        try {
            CSIResponse response = JobSchedulerManager.getInstance().updateJob(request);
            responseObserver.onNext(response);
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void cancelFetchJob(au.coaas.csi.proto.ContextService request,
                               io.grpc.stub.StreamObserver<CSIResponse> responseObserver) {
        try {
            CSIResponse response = JobSchedulerManager.getInstance().cancelJob(request);
            responseObserver.onNext(response);
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void mapToEntity(au.coaas.csi.proto.ContextMappingRequest request,
                               io.grpc.stub.StreamObserver<CSIResponse> responseObserver) {
        try {
            CSIResponse response = FetchManager.mappingService(request.getJsonResponse(),
                    request.getAttributes());
            responseObserver.onNext(response);
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
