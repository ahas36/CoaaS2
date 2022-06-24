/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package au.coaas.csi.server;
//
import au.coaas.csi.fetch.JobSchedulerManager;
import au.coaas.csi.proto.ContextService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import java.io.IOException;
import java.util.logging.Logger;

import static au.coaas.grpc.client.Config.MAX_MESSAGE_SIZE;

/**
 *
 * @author ali
 */

public class CSIServer {
    private static Logger log = Logger.getLogger(CSIServer.class.getName());

    static public void main (String [] args) throws IOException, InterruptedException, SchedulerException {
        try{
            // JobSchedulerManager jobScheduler = JobSchedulerManager.getInstance();
            // log.info("Starting");
            // log.info("Scheduler started");
            Server server = ServerBuilder.forPort(8582).addService(new CSIServiceImpl()).maxInboundMessageSize(MAX_MESSAGE_SIZE).build();
            server.start();
            // jobScheduler.start();

            // log.info(jobScheduler.registerJob(ContextService.newBuilder().setJson("test").setMongoID("12342").build()).getStatus());
            // log.info("wait...");
            // Thread.sleep(10000);
            // log.info(jobScheduler.updateJob(ContextService.newBuilder().setJson("test3").setMongoID("12342").build()).getStatus());
            // log.info("wait...");
            // Thread.sleep(10000);
            // log.info(jobScheduler.cancelJob(ContextService.newBuilder().setJson("test3").setMongoID("12342").build()).getStatus());

            log.info("Server started on port 8582");
            server.awaitTermination();
            // jobScheduler.shutdown();
        }
        catch(Exception ex){
            log.severe(ex.getMessage());
        }
    }
}
