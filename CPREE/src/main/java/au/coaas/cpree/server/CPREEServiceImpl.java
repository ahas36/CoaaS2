package au.coaas.cpree.server;

import au.coaas.cpree.proto.CPREEResponse;
import au.coaas.cpree.proto.CPREEServiceGrpc;
import au.coaas.cpree.executor.ClusteringExecutor;

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
}
