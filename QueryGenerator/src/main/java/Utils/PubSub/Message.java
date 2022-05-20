package Utils.PubSub;

import Jobs.ContextQuery;

public class Message extends Post {
    private ContextQuery query;
    public Message(ContextQuery query) {
        super(query);
    }

    public ContextQuery getQuery() {
        return query;
    }
}