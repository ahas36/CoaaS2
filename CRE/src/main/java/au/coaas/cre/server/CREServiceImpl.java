/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cre.server;

import au.coaas.cre.proto.CREResponse;
import au.coaas.cre.proto.CREServiceGrpc;
import au.coaas.cre.handlers.SituationHandler;
import au.coaas.cre.proto.CRESituation;
import au.coaas.cre.reasoner.SituationReasoner;

import java.util.logging.Logger;

/**
 *
 * @author ali
 */
public class CREServiceImpl extends CREServiceGrpc.CREServiceImplBase {

    private static Logger log = Logger.getLogger(CREServiceImpl.class.getName());

    @Override
    public void infer(au.coaas.cre.proto.SituationInferenceRequest request,
        io.grpc.stub.StreamObserver<CREResponse> responseObserver){
        try {
            responseObserver.onNext(SituationReasoner.infer(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void createSituation(au.coaas.cre.proto.SituationRequest request,
                      io.grpc.stub.StreamObserver<CRESituation> responseObserver){
        try {
            responseObserver.onNext(SituationHandler.create(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
