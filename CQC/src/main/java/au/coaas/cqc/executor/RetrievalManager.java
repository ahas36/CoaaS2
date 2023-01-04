package au.coaas.cqc.executor;

import au.coaas.cqc.utils.ConQEngHelper;
import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;
import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.Statistic;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

public class RetrievalManager {

    private static final int retrys = 20;
    // Retrieval from non-streaming Context Providers
    public static String executeFetch(String contextService, JSONObject qos, HashMap<String,String> params, String cpId) {
        CSIServiceGrpc.CSIServiceBlockingStub csiStub
                = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());

        SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        final ContextServiceInvokerRequest.Builder fetchRequest = ContextServiceInvokerRequest.newBuilder();
        fetchRequest.putAllParams(params);

        final ContextService cs = ContextService.newBuilder().setJson(contextService).build();
        fetchRequest.setContextService(cs);

        CSIResponse fetch = null;
        double penEarning = 0;
        long startTime = 0;

        for(int i=0; i < retrys; i++){
            startTime = System.currentTimeMillis();
            fetch = csiStub.fetch(fetchRequest.build());
            if(fetch.getStatus().equals("200")) break;
            else {
                long int_endTime = System.currentTimeMillis();

                // Should penalties be earned if the CP fails to respond at all (non 200 responses, timeouts)
                long retLatency = int_endTime-startTime;
                double retDiff = retLatency - qos.getDouble("rtmax");
                if(retDiff > 0){
                    penEarning = (((int)(retDiff/1000))+1) * qos.getDouble("rate")
                            * qos.getDouble("penPct") / 100;
                }

                asyncStub.logPerformanceData(Statistic.newBuilder()
                        .setIsDelayed(retDiff>0).setEarning(penEarning)
                        .setMethod("executeFetch").setStatus(fetch.getStatus())
                        .setTime(int_endTime-startTime).setCs(fetchRequest).setAge(0)
                        .setCost(!fetch.getStatus().equals("500")? fetch.getSummary().getPrice() : 0).build());
            }
        }

        // Since the end_time is after the retry attempts until success, what is being reported back to ConQEng is accumilated time.
        long endTime = System.currentTimeMillis();

        long age = 0;
        if(fetch.getStatus().equals("200")){
            JSONObject response = new JSONObject(fetch.getBody());
            if(response.has("age")){
                if(response.getString("age").startsWith("{")){
                    JSONObject age_obj = new JSONObject(response.getString("age"));

                    String unit = age_obj.getString("unitText");
                    long value = age_obj.getLong("value");

                    // Age is always considered in seconds in the code
                    switch(unit){
                        case "ms": age = value/1000; break;
                        case "s": age = value; break;
                        case "h": age = value*60; break;
                    }
                }
                else age = Long.valueOf(response.getString("age"));
            }

            // Step 3
            // Report back retrieval performance to ConQEng
            long finalAge = age;
            CSIResponse finalFetch = fetch;
            Executors.newCachedThreadPool().execute(()-> {
                        JSONObject conqEngReport = new JSONObject();
                        conqEngReport.put("age", finalAge);
                        conqEngReport.put("id", cpId);
                        conqEngReport.put("Ca", new JSONObject(finalFetch.getBody()));
                        ConQEngHelper.reportPerformance(conqEngReport);
                    }
            );

            long retLatency = endTime-startTime;
            double retDiff = retLatency - qos.getDouble("rtmax");
            if(retDiff > 0){
                penEarning = (((int)(retDiff/1000))+1) * qos.getDouble("rate")
                        * qos.getDouble("penPct") / 100;
            }

            asyncStub.logPerformanceData(Statistic.newBuilder()
                    .setMethod("executeFetch").setStatus(fetch.getStatus())
                    .setTime(retLatency).setCs(fetchRequest).setAge(age)
                    .setIsDelayed(retDiff>0).setEarning(penEarning)
                    .setCost(fetch.getStatus().equals("200")? fetch.getSummary().getPrice() : 0).build());

            return fetch.getBody();
        }

        // Returning null means the CMP failed to retrieved context from the provider despite all attempts.
        return null;
    }

    // Retrieval from streaming Context Providers
    public static String executeStreamRead(String contextService, JSONObject qos, HashMap<String,String> params){
        long startTime = System.currentTimeMillis();

        SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());

        // TODO: Implement this method to retrieve data from the storage the stream written to.
        // SQEMResponse fetch = sqemStub.fetchStreamedData();

        long endTime = System.currentTimeMillis();

        long age = 0;
//        if(fetch.getStatus().equals("200")){
//            JSONObject response = new JSONObject(fetch.getBody());
//            if(response.has("age")){
//                if(response.getString("age").startsWith("{")){
//                    JSONObject age_obj = new JSONObject(response.getString("age"));
//
//                    String unit = age_obj.getString("unitText");
//                    long value = age_obj.getLong("value");
//
//                    // Age is always considered in seconds in the code
//                    switch(unit){
//                        case "ms": age = value/1000; break;
//                        case "s": age = value; break;
//                        case "h": age = value*60; break;
//                    }
//                }
//                else age = Long.valueOf(response.getString("age"));
//            }
//        }
//
//        SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
//                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
//        // TODO: Set Context Service details
//        asyncStub.logPerformanceData(Statistic.newBuilder()
//                .setMethod("executeStreamRead").setStatus(fetch.getStatus())
//                .setTime(endTime-startTime).setEarning(0).setAge(age)
//                .setCost(fetch.getStatus().equals("200")? Double.valueOf(fetch.getMeta()) : 0).build());
//
//        if (fetch.getStatus().equals("200")) {
//            return fetch.getBody();
//        }

        return null;
    }
}
