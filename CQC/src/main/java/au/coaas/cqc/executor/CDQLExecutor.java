package au.coaas.cqc.executor;

import au.coaas.cqc.proto.ExecutionRequest;
import au.coaas.cqp.proto.*;
import au.coaas.cqc.proto.CdqlResponse;

import au.coaas.grpc.client.CQPChannel;
import au.coaas.grpc.client.SQEMChannel;

import au.coaas.sqem.proto.CDQLLog;
import au.coaas.sqem.proto.RegisterPushQuery;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CDQLExecutor {

    private static Logger log = Logger.getLogger(CDQLExecutor.class.getName());

    // Execute a PULL based query or make a subscription using a PUSH based query.
    public static CdqlResponse execute(ExecutionRequest request) throws Exception{

        // First logs the entire query as it is
        Executors.newCachedThreadPool().execute(() -> logQuery(request.getCdql(),request.getQueryid()));

        // Parse the incoming query
        CQPServiceGrpc.CQPServiceBlockingStub stub
                = CQPServiceGrpc.newBlockingStub(CQPChannel.getInstance().getChannel());
        CDQLConstruct cdqlConstruct = stub.parse(ParseRequest.newBuilder().setCdql(request.getCdql())
                .setQueryId(request.getQueryid()).build());

        switch (cdqlConstruct.getType()) {
            case QUERY:
                CDQLQuery query = cdqlConstruct.getQuery();

                // Then execute based on whether the query is PUSH or PULL.
                if(query.getQueryType().equals(QueryType.PULL_BASED))
                {
                    return PullBasedExecutor.executePullBaseQuery(query, request.getToken(),
                            request.getPage(), request.getLimit(), request.getQueryid(),
                            request.getCriticality(), cdqlConstruct.getComplexity(), null, null, null);
                } else {
                    return PushBasedExecutor.executePushBaseQuery(query, request.getToken(),
                            request.getQueryid(), request.getCriticality(), cdqlConstruct.getComplexity());
                }
            case FUNCTION_DEF:
                ContextFunction cFunction = cdqlConstruct.getFunction();
                if(cFunction.getType().equals(ContextFunctionType.SITUATION))
                {
                    return SituationRegistrationManager.register(request.getCdql(), request.getToken(), cFunction, request.getQueryid());
                }else {
                    return null;
                }
            default:
        }
        return null;
    }

    // Unsubscribe from an existing push query subscription.
    public static CdqlResponse unsubscribe (ExecutionRequest request) {
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        RegisterPushQuery response = stub.unsubscribePushQuery(
                RegisterPushQuery.newBuilder().setMessage(request.getQueryid()).build());
        return CdqlResponse.newBuilder().setStatus(response.getStatus()).build();
    }

    public static void logQuery(String query, String queryId) {
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse response = stub.logQuery(CDQLLog.newBuilder()
                .setRawQuery(query).setQueryId(queryId).build());

        if(!response.getStatus().equals("200")){
            log.log(Level.WARNING, "Query logging failed with an error.");
        }
    }
}
