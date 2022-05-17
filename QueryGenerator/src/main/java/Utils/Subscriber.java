package Utils;

public class Subscriber {
    String name;
    public Subscriber(String name) {
        this.name = name;
    }

    @OnMessage
    private void onMessage(Message message) {
        System.out.println(message.query);
    }
}
