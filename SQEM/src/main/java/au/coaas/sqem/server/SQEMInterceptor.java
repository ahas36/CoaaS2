package au.coaas.sqem.server;

import au.coaas.sqem.handler.PerformanceLogHandler;
import au.coaas.sqem.monitor.LogicalContextLevel;
import au.coaas.sqem.proto.SQEMResponse;
import io.grpc.*;

import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class SQEMInterceptor implements ServerInterceptor {

    private static final String REQUEST = "Request";
    private static final String RESPONSE = "Response";
    private static final Logger logger = Logger.getLogger(SQEMInterceptor.class.getName());

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call, final Metadata requestHeaders, ServerCallHandler<ReqT, RespT> next) {

        long startTime = System.currentTimeMillis();

        MethodDescriptor<ReqT, RespT> methodDescriptor = call.getMethodDescriptor();
        String funcName = methodDescriptor.getFullMethodName().replace("au.coaas.sqem.proto.SQEMService/","");

        logMessage(REQUEST, methodDescriptor);

        return next.startCall(new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
            @Override
            public void sendMessage(RespT message) {
                super.sendMessage(message);
                long endTime = System.currentTimeMillis();
                Executors.newCachedThreadPool().execute(() ->
                    logMessage(RESPONSE, funcName, message, endTime - startTime));
            }

        }, requestHeaders);
    }

    private <T> void logMessage(String type, String method, T message, long responseTime) {
        // Asynchronously update the performance logs
        try{
            if(message != null && !message.toString().equals("")){
                switch(method){
                    case "handleContextRequest":
                    case "discoverMatchingServices":
                    case "refreshContextEntity": {
                        // Log response time
                        SQEMResponse res = (SQEMResponse) message;
                        PerformanceLogHandler.genericRecord(method, res.getStatus(), responseTime);
                        break;
                    }
                    case "handleContextRequestInCache": {
                        // LogicalContextLevel level, String id, Boolean isHit, long rTime
                        SQEMResponse res = (SQEMResponse) message;
                        String status = res.getStatus();
                        if(!status.equals("500")){
                            if(status.equals("200") || status.equals("404")){
                                boolean ishit = status.equals("200");
                                if(res.getHashKey().startsWith("entity")){
                                    String[] keys = res.getHashKey().split(",");
                                    for (String hk: keys) {
                                        PerformanceLogHandler.insertRecord(LogicalContextLevel.ENTITY,
                                                hk.replace("entity:",""),
                                                ishit, responseTime);
                                    }
                                }
                                else if (res.getHashKey().startsWith("service")){
                                    // Those that start with 'service'.

                                }
                            }
                            else if (status.equals("400")){
                                if(res.getMisskeys().startsWith("entity")){
                                    String[] misses = res.getMisskeys().split(",");
                                    for (String hk: misses) {
                                        PerformanceLogHandler.insertRecord(LogicalContextLevel.ENTITY,
                                                hk.replace("entity:",""),
                                                false, responseTime);
                                    }
                                    if(!res.getHashKey().isEmpty() && res.getHashKey().startsWith("entity")){
                                        String[] hits = res.getHashKey().split(",");
                                        for (String hk: hits) {
                                            PerformanceLogHandler.insertRecord(LogicalContextLevel.ENTITY,
                                                    hk.replace("entity:",""),
                                                    true, responseTime);
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case "handleSituationInCache": {
                        // LogicalContextLevel level, String id, Boolean isHit, long rTime
                        SQEMResponse res = (SQEMResponse) message;
                        String status = res.getStatus();
                        if(!status.equals("500")){
                            if(status.equals("200")){
                                PerformanceLogHandler.insertRecord(LogicalContextLevel.SITUFUNCTION,
                                        res.getHashKey(), true, responseTime);
                            }
                            else {
                                PerformanceLogHandler.insertRecord(LogicalContextLevel.SITUFUNCTION,
                                        res.getMisskeys(), false, responseTime);
                            }
                        }
                        break;
                    }
                    case "logPerformanceData": break;
                    default:
                        log(String.format("%s responded in %d ms.", method, responseTime));
                        log(String.format("%s message : %s", type, message));
                        break;
                }
            }
        }
        catch(Exception ex){
            log(ex.getMessage());
        }
    }

    private <T> void logMessage(String type, T message) {
        // Asynchronously update the performance logs
        log(String.format("%s message : %s", type, message));
    }

    protected void log(String message) { logger.info(message); }
}
