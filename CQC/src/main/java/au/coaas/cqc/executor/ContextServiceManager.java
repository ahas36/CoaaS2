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
                JSONObject sub_edge = sqemBody.getJSONObject("subEdge");

                CSIServiceGrpc.CSIServiceBlockingStub csiStub
                        = CSIServiceGrpc.newBlockingStub(
                                CSIChannel.getInstance(sub_edge.getString("ipAddress"))
                                        .getChannel());

                ContextService.Builder csMessage = ContextService.newBuilder()
                        .setMongoID(id).setJson(service).setTimes(-1);
                // Tagging to update registry of the context provider location.
                if(sub_edge.getString("ipAddress").equals("0.0.0.0") &&
                        sub_edge.getInt("id") == 0)
                    csMessage.setUpdateRegistry(true);

                CSIResponse fetchJob = csiStub.createFetchJob(csMessage.build());
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
        try {
            GeoIndexer indexer = GeoIndexer.getInstance();
            if(location.equals(null) || location.isEmpty()) {
                Object res = Utilities.getPropertyValue(service, "info.location");
                if(!res.equals(null)) {
                    // This way, the resolution is low enough that we can resolve the best parent index for it to service.
                    CordinatesIndex retIndex = indexer.getGeoIndex(((JSONObject) res).getDouble("latitude"),
                            ((JSONObject) res).getDouble("longitude"));
                    index = retIndex.getIndex();
                }
                /** Note: */
                // Null index means that there is no location information currently available.
                // Possibly, the context provider is mobile (non-stationary) and the location need to resolved
                // from the retrieved response.
            }
            else {
                String[] latlng = location.split(";");
                CordinatesIndex retIndex = indexer.getGeoIndex(Double.valueOf(latlng[0]), Double.valueOf(latlng[1]));
                index = retIndex.getIndex();
            }
        } catch(Exception ex) {
            log.severe("Could not resolve the index for the given coordinates.");
        }
        return index;
    }
}
