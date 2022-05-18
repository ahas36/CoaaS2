package Utils.PubSub;

import Jobs.ContextQuery;

abstract class Post {
    ContextQuery query;

    Post(ContextQuery query) {
        this.query = query;
    }
}
