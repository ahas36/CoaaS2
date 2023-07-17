package au.coaas.cre.handlers;

import au.coaas.cre.proto.CRESituation;
import au.coaas.cre.proto.ContextEvent;
import au.coaas.cre.proto.SituationRequest;

import io.siddhi.core.event.Event;

import org.json.JSONObject;
import java.util.logging.Logger;

public class SituationHandler {
    private static Logger log = Logger.getLogger(SituationHandler.class.getName());
    public static int totalNumberOfEvents = 0;

    public static CRESituation create(SituationRequest eventRequest) {
        // Counter of all events.
        totalNumberOfEvents++;

        // Starting timer for event processing
        log.info("Started to process event at: " + System.currentTimeMillis());

        JSONObject persEvent = new JSONObject(eventRequest.getEvent());

        // If there isn't a push context query subscription attached to it, then nothing happens in this method.
        if (persEvent.has("subscriptionID") && persEvent.getString("subscriptionID") != null) {
            try {
                SiddhiWrapper.addEvent(new Object[]{"event", Double.valueOf(persEvent.getString("subscriptionValue")),
                        persEvent.getLong("timestamp")},"sub_" + persEvent.getString("subscriptionID"));
            } catch (InterruptedException ex) {
                log.severe("Error when adding event: " + ex.getStackTrace());
            }

            return CRESituation.newBuilder().setStatus("200").setBody("[]").build();
        }

        return CRESituation.newBuilder().setStatus("404").build();
    }

    public static CRESituation getResults(ContextEvent event){
        // Event eventList = SiddhiWrapper.getResults("sub_" + event.getSubscriptionID(), event.getFunctionString());
        Event eventList = SiddhiWrapper.getResults("sub_" + event.getSubscriptionID(),
                event.getFunctionName());
        try{
            return CRESituation.newBuilder().setStatus("200")
                    .setBody(eventList.getData().toString()).build();
        }
        catch(NullPointerException ex){
            log.info("No previous data available in Siddhi for this subscribed function.");
            return CRESituation.newBuilder().setStatus("200")
                    .setBody("[]").build();
        }
    }
}
