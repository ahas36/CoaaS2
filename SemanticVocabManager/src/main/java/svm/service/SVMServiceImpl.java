/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package svm.service;

import au.coaas.svm.proto.*;
import svm.jenna.ConnectionManager;

import java.util.List;
import java.util.logging.Logger;

/**
 * @author ali
 */
public class SVMServiceImpl extends SVMServiceGrpc.SVMServiceImplBase {

    private static Logger log = Logger.getLogger(SVMServiceImpl.class.getName());

    private ListOfString toListOfString(List<String> list)
    {
        return ListOfString.newBuilder().addAllValue(list).build();
    }

    @Override
    public void getClasses(au.coaas.svm.proto.SemanticVocabURL request,
                           io.grpc.stub.StreamObserver<au.coaas.svm.proto.ListOfString> responseObserver) {
        try {
            responseObserver.onNext(this.toListOfString(ConnectionManager.getClasses(request.getUrl())));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void registerVocabulary(au.coaas.svm.proto.SemanticVocabClass request,
                                   io.grpc.stub.StreamObserver<au.coaas.svm.proto.SemanticVocabRegisterationResponse> responseObserver) {
        try {
            responseObserver.onNext(ConnectionManager.register(request.getOntologyClass(),request.getUrl()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getTerms(au.coaas.svm.proto.SemanticVocabClass request,
                         io.grpc.stub.StreamObserver<au.coaas.svm.proto.Terms> responseObserver) {
        try {
            responseObserver.onNext(Terms.newBuilder().setBody(ConnectionManager.getTerms(request.getUrl(),request.getOntologyClass()).toString()).build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getParentClasses(au.coaas.svm.proto.SemanticVocabClass request,
                              io.grpc.stub.StreamObserver<au.coaas.svm.proto.ListOfString> responseObserver) {
        try {
            responseObserver.onNext(this.toListOfString(ConnectionManager.getParentClasses(request.getUrl(),request.getOntologyClass())));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getGraphs(au.coaas.svm.proto.empty request,
                                 io.grpc.stub.StreamObserver<au.coaas.svm.proto.ListOfString> responseObserver) {
        try {
            responseObserver.onNext(this.toListOfString(ConnectionManager.getGraphs()));
        } catch (Exception ex) {
            log.info(ex.getMessage());
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

}
