/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.sqem.server;


import au.coaas.sqem.handler.*;
import au.coaas.sqem.proto.Chunk;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import com.google.protobuf.ByteString;

import java.util.logging.Logger;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 * @author ali
 */
public class SQEMServiceImpl extends SQEMServiceGrpc.SQEMServiceImplBase {

    private static Logger log = Logger.getLogger(SQEMServiceImpl.class.getName());


    @Override
    public void handleContextRequest(au.coaas.sqem.proto.ContextRequest request,
                                     io.grpc.stub.StreamObserver<au.coaas.sqem.proto.Chunk> responseObserver) {
        try {
            SQEMResponse response = ContextRequestHandler.handle(request);
            byte[] bytes = response.toByteArray();

            int size = bytes.length;

            int total = (int)Math.ceil((bytes.length * 1.0) / MAX_MESSAGE_SIZE);

            for (int i = 0 ; i< total; i++){
                Chunk.Builder chunk = Chunk.newBuilder().setTotal(size).setIndex(i).setData(ByteString.copyFrom(bytes,i*MAX_MESSAGE_SIZE,
                        Math.min(size,(i+1)*MAX_MESSAGE_SIZE)));
                responseObserver.onNext(chunk.build());
            }
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }


    ///// Context Entity /////

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
            responseObserver.onNext(ContextEntityHandler.updateEntities(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    /**
     */
    @Override
    public void getAllEntityTypes(au.coaas.sqem.proto.Empty request,
                                  io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextEntityHandler.getAllTypes());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    /**
     */
    @Override
    public void clearEntityType(au.coaas.sqem.proto.EntityTypeRequest request,
                                io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextEntityHandler.clear(request.getName()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    /**
     */
    @Override
    public void removeEntityType(au.coaas.sqem.proto.EntityTypeRequest request,
                                 io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextEntityHandler.remove(request.getName()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }


    ////////// Context Service ///////////

    @Override
    public void getAllContextServices(au.coaas.sqem.proto.Empty request,
                                      io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextServiceHandler.getAllServices());
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
            responseObserver.onNext(ContextServiceHandler.changeStatus(request.getId(), request.getStatus()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();

    }

    @Override
    public void discoverMatchingServices(au.coaas.sqem.proto.ContextServiceRequest request,
                                         io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextServiceHandler.discoverMatchingServices(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    ////////// Subscriptions ///////////

    @Override
    public void getAllSubscriptions(au.coaas.sqem.proto.Empty request,
                                    io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(SubscriptionHandler.getAllSubscriptions());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    ////////// Situations ///////////

    @Override
    public void getAllSituations(au.coaas.sqem.proto.Empty request,
                                 io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(SituationHandler.getAllSituationFunctions());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();

    }

    @Override
    public void registerSituationFunction(au.coaas.sqem.proto.RegisterSituationRequest request,
                                          io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(SituationHandler.register(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void findSituationByTitle(au.coaas.sqem.proto.SituationFunctionRequest request,
                                     io.grpc.stub.StreamObserver<au.coaas.cqp.proto.SituationFunction> responseObserver) {
        try {
            responseObserver.onNext(SituationHandler.findSituationByTitle(request.getName()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }


    /////// Logs functions

    @Override
    public void getAllQueryLogs(au.coaas.sqem.proto.Empty request,
                                 io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(LogHandler.getAllLogs());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void logQuery(au.coaas.sqem.proto.CDQLLog request,
                                io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(LogHandler.logQuery(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

}
