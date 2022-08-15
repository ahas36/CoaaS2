package au.coaas.cpree.executor.scheduler.jobs;

import au.coaas.csi.proto.CSIResponse;
import au.coaas.csi.proto.CSIServiceGrpc;
import au.coaas.csi.proto.ContextService;
import au.coaas.csi.proto.ContextServiceInvokerRequest;

import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.Statistic;

import au.coaas.grpc.client.CSIChannel;
import au.coaas.grpc.client.SQEMChannel;

import org.json.JSONObject;

import java.util.HashMap;

public class RetrievalManager {

    // Retrieval from non-streaming Context Providers
    public static String executeFetch(String contextService, HashMap<String,String> params) {
        long startTime = System.currentTimeMillis();

        CSIServiceGrpc.CSIServiceBlockingStub csiStub
                = CSIServiceGrpc.newBlockingStub(CSIChannel.getInstance().getChannel());

        final ContextServiceInvokerRequest.Builder fetchRequest = ContextServiceInvokerRequest.newBuilder();
        fetchRequest.putAllParams(params);

        final ContextService cs = ContextService.newBuilder().setJson(contextService).build();
        fetchRequest.setContextService(cs);
        CSIResponse fetch = csiStub.fetch(fetchRequest.build());

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
        }

        SQEMServiceGrpc.SQEMServiceBlockingStub asyncStub
                = SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
        asyncStub.logPerformanceData(Statistic.newBuilder()
                .setMethod("executeFetch").setStatus(fetch.getStatus())
                .setTime(endTime-startTime).setCs(fetchRequest).setAge(age)
                .setCost(fetch.getStatus().equals("200")? fetch.getSummary().getPrice() : 0).build());
        // Here, the response to fetch is not 200, there is not monetary cost, but there is an abstract cost of network latency

        if (fetch.getStatus().equals("200")) {
            return fetch.getBody();
        }

        return null;
    }

    // Retrieval from streaming Context Providers
    public static String executeStreamRead(String contextService, HashMap<String,String> params){
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
