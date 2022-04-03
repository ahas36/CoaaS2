package au.coaas.cqc.executor;

import au.coaas.cqp.proto.*;
import au.coaas.cqc.proto.CdqlResponse;

import au.coaas.grpc.client.CQPChannel;
import au.coaas.grpc.client.SQEMChannel;

import au.coaas.sqem.proto.CDQLLog;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;

import java.util.logging.Level;
import java.util.logging.Logger;

public class CDQLExecutor {

    private static Logger log = Logger.getLogger(CDQLExecutor.class.getName());

    public static CdqlResponse execute(String cdql, int page, int limit, String queryId) {

        // First logs the entire query as it is
        logQuery(cdql,queryId);

        // Parse the incoming query
        CQPServiceGrpc.CQPServiceBlockingStub stub
                = CQPServiceGrpc.newBlockingStub(CQPChannel.getInstance().getChannel());
        CDQLConstruct cdqlConstruct = stub.parse(ParseRequest.newBuilder().setCdql(cdql).setQueryId(queryId).build());

        switch (cdqlConstruct.getType()) {
            case QUERY:
                CDQLQuery query = cdqlConstruct.getQuery();

                // Then execute based on whether the query is Push or Pull.
                if(query.getQueryType().equals(QueryType.PULL_BASED))
                {
                    return PullBasedExecutor.executePullBaseQuery(query,page,limit);
                }else {
                    return PushBasedExecutor.executePushBaseQuery(query);
                }
            case FUNCTION_DEF:
                ContextFunction cFunction = cdqlConstruct.getFunction();
                if(cFunction.getType().equals(ContextFunctionType.SITUATION))
                {
                    return SituationRegistrationManager.register(cdql,cFunction);
                }else {
                    return null;
                }
            default:
        }
        return null;
    }

    public static void logQuery(String query, String queryId){
        SQEMServiceGrpc.SQEMServiceBlockingStub stub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        SQEMResponse response = stub.logQuery(CDQLLog.newBuilder().setRawQuery(query).setQueryId(queryId).build());
        if(!response.getStatus().equals("200")){
            log.log(Level.WARNING,response.getBody());
        }
    }
}
