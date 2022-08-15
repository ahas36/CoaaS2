package au.coaas.cpree.executor.scheduler;

import au.coaas.cpree.utils.PubSub.OnMessage;
import au.coaas.cpree.utils.PubSub.Message;
import org.quartz.SchedulerException;

public class Subscriber {
    String name;
    public Subscriber(String name) {
        this.name = name;
    }

    @OnMessage
    private void onMessage(Message message) throws SchedulerException {
        RefreshScheduler.getInstance().scheduleRefresh(message.getQuery());
    }
}
