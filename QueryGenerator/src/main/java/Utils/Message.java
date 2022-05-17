package Utils;

import Jobs.ContextQuery;

public class Message extends Post {
    ContextQuery query;

    public Message(ContextQuery query) {
        super(query);
    }
}