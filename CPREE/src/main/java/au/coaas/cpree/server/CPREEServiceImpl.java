package au.coaas.cpree.server;

import au.coaas.cpree.executor.scheduler.jobs.RetrievalManager;
import au.coaas.cpree.proto.CPREEResponse;
import au.coaas.cpree.proto.CPREEServiceGrpc;
import au.coaas.cpree.executor.SelectionExecutor;
import au.coaas.cpree.executor.ClusteringExecutor;
import au.coaas.cpree.executor.RefreshExecutor;
import au.coaas.cpree.proto.Empty;

import java.util.logging.Logger;

public class CPREEServiceImpl extends CPREEServiceGrpc.CPREEServiceImplBase {
    private static Logger log = Logger.getLogger(CPREEServiceImpl.class.getName());

    @Override
    public void classifyQuery(au.coaas.cpree.proto.ContextQueryPlan request,
                        io.grpc.stub.StreamObserver<CPREEResponse> responseObserver){
        try {
            responseObserver.onNext(ClusteringExecutor.execute(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void evaluateAndCacheContext(au.coaas.cpree.proto.CacheSelectionRequest request,
                              io.grpc.stub.StreamObserver<CPREEResponse> responseObserver){
        try {
            responseObserver.onNext(SelectionExecutor.execute(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void refreshContext(au.coaas.cpree.proto.ContextRefreshRequest request,
                                        io.grpc.stub.StreamObserver<au.coaas.cpree.proto.CPREEResponse> responseObserver){
        try {
            responseObserver.onNext(RefreshExecutor.refreshContext(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void updateWeights(au.coaas.cpree.proto.LearnedWeights request,
                               io.grpc.stub.StreamObserver<au.coaas.cpree.proto.Empty> responseObserver){
        try {
            responseObserver.onNext(SelectionExecutor.updateWeights(request));
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void stopRefreshing(au.coaas.cpree.proto.Lookup request,
                              io.grpc.stub.StreamObserver<au.coaas.cpree.proto.Empty> responseObserver){
        try {
            responseObserver.onNext(RefreshExecutor.stopProactiveRefreshing(request.getContextId()));
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

    @Override
    public void modifyCPMonitor(au.coaas.cpree.proto.CPMonitor request,
                               io.grpc.stub.StreamObserver<au.coaas.cpree.proto.Empty> responseObserver){
        try {
            RetrievalManager.updateMonitored(request.getContextID(),
                    request.getContextEntity(), request.getDelete());
            responseObserver.onNext(Empty.newBuilder().build());
        } catch (Exception ex) {
            responseObserver.onError(ex);
            log.severe(ex.getMessage());
        }
        responseObserver.onCompleted();
    }

}
