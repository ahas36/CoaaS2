package au.coaas.cpree.executor;

import au.coaas.cpree.proto.CPREEResponse;
import au.coaas.cpree.proto.ContextQueryPlan;

/**
 * @author shakthi
 */
public class ClusteringExecutor {
    public static CPREEResponse execute(ContextQueryPlan request){
        // TODO: Implement Logic
        return CPREEResponse.newBuilder().setStatus("404").build();
    }
}
