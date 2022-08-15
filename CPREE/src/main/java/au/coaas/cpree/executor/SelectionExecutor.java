package au.coaas.cpree.executor;

import au.coaas.cpree.proto.CPREEResponse;
import au.coaas.cpree.proto.CacheSelectionRequest;
import au.coaas.cpree.utils.enums.RefreshLogics;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.CacheRequest;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import org.json.JSONObject;

public class SelectionExecutor {
    // TODO: Cache Selection Logic
    // This is where the Expected values should be considered (if there is a history)
    public static CPREEResponse execute(CacheSelectionRequest request){
        RefreshLogics ref_type = RefreshExecutor.resolveRefreshLogic(new JSONObject(request.getSla()));
        if(ref_type.equals(RefreshLogics.PROACTIVE_SHIFT))
            RefreshExecutor.setProactiveRefreshing(request);

        // Actual Caching
        SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        asyncStub.cacheEntity(CacheRequest.newBuilder()
                .setJson(request.getContext())
                .setRefreshLogic(ref_type.toString().toLowerCase())
                // TODO: This cache life should be saved in the eviction registry
                .setCachelife(600)
                .setReference(request.getReference()).build());

        return CPREEResponse.newBuilder().setStatus("200").build();
    }
}
