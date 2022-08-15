package au.coaas.cpree.executor.scheduler.jobs;

import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import au.coaas.sqem.proto.CacheRefreshRequest;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;

import java.util.logging.Logger;

public class QueryJob implements Job {

    private static Logger log = Logger.getLogger(QueryJob.class.getName());

    public void execute(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        String data = dataMap.getString("data");

        SQEMServiceGrpc.SQEMServiceFutureStub asyncStub
                = SQEMServiceGrpc.newFutureStub(SQEMChannel.getInstance().getChannel());
        // asyncStub.refreshContextEntity(CacheRefreshRequest.newBuilder()
        //        .setReference(lookup)
        //        .setJson(data).build());

        // Should reset the execution time of the tigger.
    }
}
