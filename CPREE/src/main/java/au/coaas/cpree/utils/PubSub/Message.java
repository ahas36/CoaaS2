package au.coaas.cpree.utils.PubSub;

import au.coaas.cpree.executor.scheduler.RefreshContext;

public class Message extends Post {
    private RefreshContext query;
    public Message(RefreshContext query) {
        super(query);
        this.query = query;
    }

    public RefreshContext getQuery() {
        return query;
    }
}