package au.coaas.cpree.executor.scheduler.jobs;

public class RefreshContext {

    private String data;
    private String contextId;
    private long refreshInterval; // This is in miliseconds

    // Refresh Interval is specific to the next retrieval only because,
    // refInteral = Lifetime - age - retrievalLatency
    public RefreshContext(String data, String contextId, double refInterval){
        this.data = data;
        this.contextId = contextId;
        // The parameter should be set in seconds.
        this.refreshInterval = (long) refInterval * 1000;
    }

    public String getData(){
        return this.data;
    }
    public String getContextId(){
        return this.contextId;
    }
    public long getRefreshInterval() { return this.refreshInterval; }
}
