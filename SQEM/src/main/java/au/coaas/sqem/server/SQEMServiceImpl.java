/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.sqem.server;

import au.coaas.sqem.handler.*;
import au.coaas.sqem.proto.Chunk;
import au.coaas.sqem.proto.Empty;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import com.google.protobuf.ByteString;

import java.util.logging.Logger;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

/**
 * @author ali
 */
public class SQEMServiceImpl extends SQEMServiceGrpc.SQEMServiceImplBase {

    private static Logger log = Logger.getLogger(SQEMServiceImpl.class.getName());

    @Override
    public void handleContextRequest(au.coaas.sqem.proto.ContextRequest request,
                                     io.grpc.stub.StreamObserver<au.coaas.sqem.proto.Chunk> responseObserver) {
        try {
            // Executing context-request for an entity
            SQEMResponse response = ContextRequestHandler.handle(request);
            byte[] bytes = response.toByteArray();

            int size = bytes.length;

            // Breaking the message into manageable chunks for sharing among services
            // Maximum message size is 50MB
            int total = (int)Math.ceil((bytes.length * 1.0) / MAX_MESSAGE_SIZE);

            for (int i = 0 ; i< total; i++){
                Chunk.Builder chunk = Chunk.newBuilder().setTotal(size).setIndex(i).setData(ByteString.copyFrom(bytes,i*MAX_MESSAGE_SIZE,
                        Math.min(size,(i+1)*MAX_MESSAGE_SIZE)));
                responseObserver.onNext(chunk.build());
            }
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    ///// Context Cache /////

    @Override
    public void cacheEntity(au.coaas.sqem.proto.CacheRequest request,
                                      io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextCacheHandler.cacheEntity(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void refreshContextEntity(au.coaas.sqem.proto.CacheRefreshRequest request,
                                    io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextCacheHandler.refreshEntity(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void toggleRefreshLogic(au.coaas.sqem.proto.RefreshUpdate request,
                                     io.grpc.stub.StreamObserver<au.coaas.sqem.proto.Empty> responseObserver) {
        try {
            responseObserver.onNext(ContextCacheHandler.toggleRefreshLogic(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void evictContextEntity(au.coaas.sqem.proto.CacheLookUp request,
                                     io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextCacheHandler.evictEntity(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void evictContextEntityByHashKey(au.coaas.sqem.proto.ContextServiceId request,
                                   io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextCacheHandler.evictEntity(request.getId()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void handleContextRequestInCache(au.coaas.sqem.proto.CacheLookUp request,
                                            io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            // Retrieving context for entity from cache
            responseObserver.onNext(ContextCacheHandler.retrieveFromCache(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void clearCache(au.coaas.sqem.proto.Empty request,
                                  io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextCacheHandler.clearCache());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getcachePerformance(au.coaas.sqem.proto.Empty request,
                           io.grpc.stub.StreamObserver<au.coaas.sqem.proto.CachePerformance> responseObserver) {
        try {
            responseObserver.onNext(ContextCacheHandler.getCachePerformance());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void logDecisionLatency(au.coaas.sqem.proto.DecisionLog request,
                                    io.grpc.stub.StreamObserver<au.coaas.sqem.proto.Empty> responseObserver) {
        try {
            responseObserver.onNext(ContextCacheHandler.logCacheDecisionLatency(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void logCacheDecision(au.coaas.sqem.proto.ContextCacheDecision request,
                                   io.grpc.stub.StreamObserver<au.coaas.sqem.proto.Empty> responseObserver) {
        try {
            responseObserver.onNext(ContextCacheHandler.logCacheDecision(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }



    @Override
    public void getCacheLatenciesSummary(au.coaas.sqem.proto.Empty request,
                                   io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(PerformanceLogHandler.getCacheLifeSummary());
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
    public void getContextServiceInfo(au.coaas.sqem.proto.ContextServiceId request,
                                      io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextServiceHandler.getContextServiceInfo(request.getId()));
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


    ////////// Log Functions ///////////
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

    @Override
    public void logParsedQueryTree(au.coaas.sqem.proto.CDQLLog request,
                         io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(LogHandler.logQueryTree(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void logPerformanceData(au.coaas.sqem.proto.Statistic request,
                                   io.grpc.stub.StreamObserver<au.coaas.sqem.proto.Empty> responseObserver){
        try {
            PerformanceLogHandler.coassPerformanceRecord(request);
            responseObserver.onNext(Empty.newBuilder().build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void logCPREEData(au.coaas.sqem.proto.Statistic request,
                                   io.grpc.stub.StreamObserver<au.coaas.sqem.proto.Empty> responseObserver){
        try {
            PerformanceLogHandler.cpreePerformanceRecord(request);
            responseObserver.onNext(Empty.newBuilder().build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void logConQEngCR(au.coaas.sqem.proto.ConQEngLog request,
                         io.grpc.stub.StreamObserver<au.coaas.sqem.proto.Empty> responseObserver) {
        try {
            responseObserver.onNext(LogHandler.logConQEng(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void logConsumerProfile(au.coaas.sqem.proto.SummarySLA request,
                                   io.grpc.stub.StreamObserver<au.coaas.sqem.proto.Empty> responseObserver){
        try {
            PerformanceLogHandler.consumerRecord(request);
            responseObserver.onNext(Empty.newBuilder().build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getPerformanceData(au.coaas.sqem.proto.Empty request,
                                   io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(PerformanceLogHandler.getCurrentPerformanceSummary());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getModelState(au.coaas.sqem.proto.Empty request,
                                   io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(PerformanceLogHandler.getModelStateVariation());
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getContextProviderProfile(au.coaas.sqem.proto.ContextProfileRequest request,
                                   io.grpc.stub.StreamObserver<au.coaas.sqem.proto.ContextProviderProfile> responseObserver) {
        try {
            responseObserver.onNext(PerformanceLogHandler
                    .getContextProviderProfile(request.getPrimaryKey(), request.getHashKey()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getContextProfile(au.coaas.sqem.proto.ContextProfileRequest request,
                                          io.grpc.stub.StreamObserver<au.coaas.sqem.proto.ContextProfile> responseObserver) {
        try {
            responseObserver.onNext(PerformanceLogHandler
                    .getContextProfile(request.getPrimaryKey(), request.getLimit()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getQueryClassProfile(au.coaas.sqem.proto.ContextProfileRequest request,
                                          io.grpc.stub.StreamObserver<au.coaas.sqem.proto.QueryClassProfile> responseObserver) {
        try {
            responseObserver.onNext(PerformanceLogHandler
                    .getQueryClassProfile(request.getPrimaryKey()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getProbDelay(au.coaas.sqem.proto.ProbDelayRequest request,
                                     io.grpc.stub.StreamObserver<au.coaas.sqem.proto.ProbDelay> responseObserver) {
        try {
            responseObserver.onNext(PerformanceLogHandler.getProbabilityOfDelay(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    ////////// Authentication & Context Consumers ///////////
    @Override
    public void authenticateConsumer(au.coaas.sqem.proto.AuthRequest request,
                                      io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(AuthHandler.getConsumer(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void saveOrUpdateToken(au.coaas.sqem.proto.AuthToken request,
                                     io.grpc.stub.StreamObserver<Empty> responseObserver) {
        try {
            responseObserver.onNext(AuthHandler.saveOrUpdateToken(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void getConsumerSLA(au.coaas.sqem.proto.AuthToken request,
                                  io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextConsumerHandler.retrieveSLA(request.getToken()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void registerContextConsumer(au.coaas.sqem.proto.RegisterContextConsumerRequest request,
                                       io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextConsumerHandler.register(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void updateContextConsumerStatus(au.coaas.sqem.proto.UpdateContextConsumerStatusRequest request,
                                           io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver) {
        try {
            responseObserver.onNext(ContextConsumerHandler.changeStatus(request.getId(), request.getStatus()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

    @Override
    public void validateToken(au.coaas.sqem.proto.AuthToken request,
                              io.grpc.stub.StreamObserver<au.coaas.sqem.proto.SQEMResponse> responseObserver){
        try {
            responseObserver.onNext(AuthHandler.validateConsumer(request.getUsername()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
        }
        responseObserver.onCompleted();
    }

}
