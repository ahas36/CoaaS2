package au.coaas.cpree.utils.PubSub;

import au.coaas.cpree.executor.scheduler.RefreshContext;

abstract class Post {
    RefreshContext data;
    Post(RefreshContext data) {
        this.data = data;
    }
}
