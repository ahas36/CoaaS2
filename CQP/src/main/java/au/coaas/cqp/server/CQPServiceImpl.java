/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.cqp.server;

import au.coaas.cqp.exception.CDQLSyntaxtErrorException;
import au.coaas.cqp.parser.MainParser;
import au.coaas.cqp.proto.CDQLConstruct;
import au.coaas.cqp.proto.CDQLQuery;
import au.coaas.cqp.proto.CQPServiceGrpc;

import java.util.logging.Logger;

/**
 *
 * @author ali
 */
public class CQPServiceImpl extends CQPServiceGrpc.CQPServiceImplBase {

    private static Logger log = Logger.getLogger(CQPServiceImpl.class.getName());

    @Override
    public void parse(au.coaas.cqp.proto.ParseRequest request,
        io.grpc.stub.StreamObserver<CDQLConstruct> responseObserver){
        try {
            //log.info("Request received");
            //log.info(request.getCdql());
            CDQLConstruct cdql = MainParser.parse(request.getCdql());
            responseObserver.onNext(cdql);
        } catch (CDQLSyntaxtErrorException ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }
}
