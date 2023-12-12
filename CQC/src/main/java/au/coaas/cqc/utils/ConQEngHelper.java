package au.coaas.cqc.utils;

import au.coaas.cqc.utils.enums.RequestDataType;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.ConQEngLog;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import okhttp3.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import au.coaas.cqc.utils.enums.HttpRequests;

import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

/**
 * @author shakthi
 */
public class ConQEngHelper {
    private static Logger log = Logger.getLogger(ConQEngHelper.class.getName());

    private static final String baseURL = "https://omega-branch-291602.ts.r.appspot.com/RRprocessor/";
    private static final String evalbaseURL = "https://omega-branch-291602.ts.r.appspot.com/QoC_CoC_Evaluator/";

    // Starts a Context Request in ConQEng.
    public static boolean createContextRequest(JSONObject cr, JSONArray contextServices, String sample){
        String result = Utilities.httpCall(baseURL + "context_requests",
                HttpRequests.POST, RequestDataType.JSON, null, cr.toString());

        if(result != null) {
            if(result.startsWith("{")){
                JSONObject crRes = new JSONObject(result);
                String crId = crRes.getString("_id");
                SQEMServiceGrpc.SQEMServiceFutureStub future =
                        SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
                future.logConQEngCR(ConQEngLog.newBuilder()
                        .setId(crId).setCr(cr.toString()).setStatus(200).build());
                Executors.newSingleThreadExecutor().submit(() -> reportContextProviders(contextServices, sample, crId));
            }

            return true;
        }
        return false;
    }

    private static void reportContextProviders(JSONArray contextServices, String sample, String crId){
        // This block threw a concurrent modification exception when used an iterator.
        for(int i = 0; i < contextServices.length(); i++){
            try {
                JSONObject fcp  = (JSONObject) contextServices.get(i);
                JSONObject conqEngSort = new JSONObject(sample);

                conqEngSort.put("pid", fcp.getJSONObject("_id").getString("$oid"));

                // The following are example SLA values given to ConQEng as reference.
                JSONObject cpQoS = fcp.getJSONObject("sla").getJSONObject("qos");
                conqEngSort.put("ctimeliness", cpQoS.getDouble("rtmax"));
                conqEngSort.put("cost", cpQoS.getDouble("rate"));
                conqEngSort.put("pen_timeliness", cpQoS.getDouble("penPct"));

                boolean res = registerCPs(conqEngSort);
                if(!res) {
                    log.severe("Failed registering Context Providers for " + crId + ".");
                    break;
                }
            }
            catch(Exception ex){
                log.severe("Error occured in registering all the context providers: " +
                        ex.getMessage());
            }
        }
    }

    // Requests the ordered list of Context Providers based on Cost and Quality from ConQEng.
    public static List<JSONObject> getCPOrder(JSONObject cpRequest, JSONArray contextServices){
        String result = Utilities.httpCall(baseURL + "context_providers",
                HttpRequests.POST, RequestDataType.JSON, null, cpRequest.toString());
        List<JSONObject> orderdCPs = new ArrayList<>();
        if(result != null) {
            JSONArray sorted = new JSONArray(result);
            for(Object cpid : sorted){
                if(contextServices.length() == 0) break;
                for(int i = 0; i < contextServices.length(); i++){
                    JSONObject cp = contextServices.getJSONObject(i);
                    if(cp.getJSONObject("_id").getString("$oid").equals((String)cpid)){
                        orderdCPs.add(cp);
                        contextServices.remove(i);
                        break;
                    }
                }
            }
        }
        return orderdCPs;
    }

    // Registers context providers in ConCQEng
    private static boolean registerCPs(JSONObject cpRequest){
        String result = Utilities.httpCall(baseURL + "context_providers",
                HttpRequests.POST, RequestDataType.JSON, null, cpRequest.toString());
        if(result != null) return true;
        return false;
    }

    // Reports back the performance of the last context retrieval based on selection to ConQEng.
    public static void reportPerformance(JSONObject perfRequest){
        Utilities.httpCall(evalbaseURL + "context_responses", HttpRequests.POST,
                RequestDataType.JSON, null, perfRequest.toString());
    }

    // Reports back failed context providers to ConQEng.
    public static void reportFeedback(JSONObject perfRequest){
        Utilities.httpCall(evalbaseURL + "feedback", HttpRequests.POST,
                RequestDataType.JSON, null, perfRequest.toString());
    }


}
