import Utils.PubSub.Message;
import Utils.PubSub.OnMessage;
import org.quartz.SchedulerException;

public class Subscriber {
    String name;
    public Subscriber(String name) {
        this.name = name;
    }

    @OnMessage
    private void onMessage(Message message) throws SchedulerException {
        QueryScheduler.getInstance().scheduleQuery(message.getQuery());
    }
}
