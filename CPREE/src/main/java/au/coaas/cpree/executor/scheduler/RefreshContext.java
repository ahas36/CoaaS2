package au.coaas.cpree.executor.scheduler;

public class RefreshContext {
    private String contextId;
    private long refreshInterval; // This is in miliseconds

    // Refresh Interval is specific to the next retrieval only because,
    // refInteral = Lifetime - age - retrievalLatency
    public RefreshContext(String contextId, double refInterval){
        this.contextId = contextId;
        // The parameter, when passed, should be set in seconds.
        this.refreshInterval = (long) refInterval * 1000;
    }

    public String getContextId(){
        return this.contextId;
    }
    public long getRefreshInterval() { return this.refreshInterval; }
}
