package au.coaas.cqc.executor;

import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqp.proto.*;
import au.coaas.grpc.client.CQPChannel;

import java.util.logging.Logger;

public class CDQLExecutor {

    private static Logger log = Logger.getLogger(CDQLExecutor.class.getName());

    public static CdqlResponse execute(String cdql) {
        //parse the incoming query
        CQPServiceGrpc.CQPServiceBlockingStub stub
                = CQPServiceGrpc.newBlockingStub(CQPChannel.getInstance().getChannel());
        CDQLConstruct cdqlConstruct = stub.parse(ParseRequest.newBuilder().setCdql(cdql).build());
        switch (cdqlConstruct.getType()) {
            case QUERY:
                CDQLQuery query = cdqlConstruct.getQuery();
                if(query.getQueryType().equals(QueryType.PULL_BASED))
                {
                    return PullBasedExecutor.executePullBaseQuery(query);
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
}
