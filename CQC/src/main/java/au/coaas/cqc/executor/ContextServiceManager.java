package au.coaas.cqc.executor;

import au.coaas.cqc.proto.CordinatesIndex;
import au.coaas.cqc.utils.GeoIndexer;
import au.coaas.cqc.utils.Utilities;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.ExecutionRequest;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextService;
import au.coaas.sqem.proto.*;
import org.json.JSONObject;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import java.util.logging.Logger;

public class ContextServiceManager {

    private static Logger log = Logger.getLogger(ContextServiceManager.class.getName());

    public static CdqlResponse registerContextService(ExecutionRequest request)
    {
        SQEMResponse sqemResponse = null;
        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub = null;
        try {
            sqemStub = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            String service = request.getCdql();
            sqemResponse = sqemStub.registerContextService(
                    RegisterContextServiceRequest.newBuilder().setJson(service)
                            .setIndex(getIndexOfService(service, request.getLocation())).build());

            if(sqemResponse.getStatus().equals("200") &&
                    new JSONObject(service).getJSONObject("sla").getBoolean("autoFetch"))
            {
                JSONObject sqemBody = new JSONObject(sqemResponse.getBody());
                String id = sqemBody.getString("id");

                CSIServiceGrpc.CSIServiceBlockingStub csiStub
                        = CSIServiceGrpc.newBlockingStub(
                            sqemBody.getString("sub_edge").equals("master") ?
                                    CSIChannel.getInstance().getChannel() :
                                    CSIChannel.getInstance(sqemBody.getString("sub_edge")).getChannel());

                CSIResponse fetchJob = csiStub.createFetchJob(
                        ContextService.newBuilder()
                                .setMongoID(id)
                                .setJson(service).setTimes(-1).build());

                if(!fetchJob.getStatus().equals("200"))
                {
                    sqemStub.updateContextServiceStatus(
                            UpdateContextServiceStatusRequest.newBuilder().setId(id).setStatus("inactive").build());
                    sqemBody.put("status","inactive");
                    sqemBody.put("cause",new JSONObject(fetchJob.getBody()));
                    return CdqlResponse.newBuilder().setBody(sqemBody.toString()).setStatus(sqemResponse.getStatus()).build();
                }
            }
            return CdqlResponse.newBuilder().setBody(sqemResponse.getBody()).setStatus(sqemResponse.getStatus()).build();
        }
        catch (Exception e)
        {
            log.severe(e.getMessage());
            if(sqemResponse!=null && sqemResponse.getStatus().equals("200"))
            {
                JSONObject sqemBody = new JSONObject(sqemResponse.getBody());
                String id = sqemBody.getString("id");
                sqemStub.updateContextServiceStatus(UpdateContextServiceStatusRequest.newBuilder().setId(id).setStatus("inactive").build());
                sqemBody.put("status","inactive");
                sqemBody.put("cause",new JSONObject(e.getMessage()));

                return CdqlResponse.newBuilder().setBody(sqemBody.toString()).setStatus(sqemResponse.getStatus()).build();
            }

            return CdqlResponse.newBuilder().setBody(sqemResponse.getBody()).setStatus(sqemResponse.getStatus()).build();
        }

    }

    private static long getIndexOfService (String service, String location) {
        Long index = 0L;
        if(location.equals(null) || location.isEmpty()) {
            Object res = Utilities.getPropertyValue(service, "info.location");
            if(!res.equals(null)) {
                GeoIndexer indexer = GeoIndexer.getInstance();
                // This way, the resolution is low enough that we can resolve the best parent index for it to service.
                CordinatesIndex retIndex = indexer.getGeoIndex(((JSONObject) res).getDouble("latitude"),
                        ((JSONObject) res).getDouble("longitude"));
                index = retIndex.getIndex();
            }
        }
        return index;
    }
}
