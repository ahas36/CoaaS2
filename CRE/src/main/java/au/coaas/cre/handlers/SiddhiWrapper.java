package au.coaas.cre.handlers;

import au.coaas.cre.proto.CRESituation;
import au.coaas.cre.proto.SiddhiRegister;
import org.wso2.siddhi.core.event.Event;
import org.wso2.siddhi.core.SiddhiManager;
import org.wso2.siddhi.core.SiddhiAppRuntime;
import org.wso2.siddhi.core.util.EventPrinter;
import org.wso2.siddhi.core.stream.input.InputHandler;
import org.wso2.siddhi.core.stream.output.StreamCallback;

import java.util.Set;
import java.util.logging.Logger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SiddhiWrapper {
    private static SiddhiManager siddhiManager;
    private static Logger log = Logger.getLogger(SiddhiWrapper.class.getName());
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);

    static StreamCallback increaseCallback = new StreamCallback() {
        @Override
        public void receive(Event[] events) {
            EventPrinter.print(events);
            log.info("Testing the increase callback!");
        }
    };

    static StreamCallback stableCallback = new StreamCallback() {
        @Override
        public void receive(Event[] events) {
            EventPrinter.print(events);
            log.info("Testing the stable callback!");
        }
    };

    static StreamCallback decreaseCallback = new StreamCallback() {
        @Override
        public void receive(Event[] events) {
            EventPrinter.print(events);
            log.info("Testing the decreasing callback!");
        }
    };

    public static void addEvent(Object[] obj, String streamName) throws InterruptedException {
        lock.readLock().lock();
        InputHandler inputHandler = getSiddhiManager()
                .getSiddhiAppRuntime(streamName).getInputHandler("subs");
        inputHandler.send(obj);
        lock.readLock().unlock();
    }

    public static CRESituation createSiddhiApp(SiddhiRegister specs) {
        try{
            SiddhiAppRuntime siddhiAppRuntime = getSiddhiManager().createSiddhiAppRuntime(specs.getJson());
            for (String usedFunction : specs.getUsedFunctionsList()) {
                switch (usedFunction) {
                    case "decrease":
                        siddhiAppRuntime.addCallback("DecreaseTrendAlertStream", decreaseCallback);
                        break;
                    case "increase":
                        siddhiAppRuntime.addCallback("IncreaseTrendAlertStream", increaseCallback);
                        break;
                    case "isValid":
                        siddhiAppRuntime.addCallback("stableTrendAlertStream", stableCallback);
                        break;
                }
            }

            siddhiAppRuntime.start();
            return CRESituation.newBuilder().setStatus("200").build();
        }
        catch(Exception ex) {
            return CRESituation.newBuilder()
                    .setBody(ex.getMessage())
                    .setStatus("500").build();
        }
    }

    public static Event getResults(String appName, String functionSignature) {
        Event[] events = getSiddhiManager().getSiddhiAppRuntime(appName).query(""
                + " from eventResultTable on  functionSignature == '" + functionSignature
                + "' select * order by timestamp desc\n limit 1");
        return events == null ? null : events[0];
    }

    public static SiddhiManager getSiddhiManager() {
        if (siddhiManager == null) {
            siddhiManager = new SiddhiManager();
        }
        return siddhiManager;
    }
}
