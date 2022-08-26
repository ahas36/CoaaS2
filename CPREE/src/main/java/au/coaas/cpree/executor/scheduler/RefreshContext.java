package au.coaas.cpree.executor.scheduler;

import au.coaas.cqp.proto.ContextEntityType;
import org.json.JSONObject;
import org.quartz.JobDataMap;

import java.util.HashMap;
import java.util.Map;

public class RefreshContext {
    private double fthr;
    private double lifetime;
    private String contextId;
    private String refreshPolicy;
    private String contextProvider;
    private ContextEntityType etype;
    private HashMap<String, String> params;

    private long initInterval;
    private long refreshInterval; // This is in miliseconds

    // Refresh Interval is specific to the next retrieval only because,
    // refInteral = Lifetime - age - retrievalLatency
    public RefreshContext(String contextId, double fthr, String refreshPolicy, double initResiLife,
                          double lifetime, HashMap<String, String> params, String contextProvider, ContextEntityType et){
        this.etype = et;
        this.fthr = fthr;
        this.params = params;
        this.lifetime = lifetime;
        this.contextId = contextId;
        this.refreshPolicy = refreshPolicy;
        this.contextProvider = contextProvider;

        JSONObject cpObj = new JSONObject(this.contextProvider);
        double samplingInt = cpObj.getJSONObject("sla").getJSONObject("updateFrequency").getDouble("value");

        // The parameter, when passed, should be set in seconds.
        double adjustment = lifetime - initResiLife;
        if(lifetime > samplingInt){
            this.refreshInterval = (long) (samplingInt + ((lifetime - samplingInt) * (1 - fthr))) * 1000;
            if(adjustment < samplingInt) {
                // If the retrieval latency + age loss is less than the sampling interval
                this.initInterval = (long) (samplingInt + ((initResiLife - samplingInt) * (1 - fthr))) * 1000;
            }
            else {
                double diff = adjustment - samplingInt;
                double exp_prd = (lifetime - samplingInt) * (1 - fthr);
                // Checking if the exp_prd has already elapsed that the item need immediatly be refreshed.
                if(exp_prd < diff) this.initInterval = 0;
                else this.initInterval = (long) (samplingInt + exp_prd) * 1000;
            }
        }
        else {
            this.refreshInterval = (long) (samplingInt) * 1000;
            this.initInterval = (long) (initResiLife * (1 - fthr)) * 1000;
        }

    }

    // Getters
    public double getfthresh() { return this.fthr; }
    public String getContextId(){ return this.contextId; }
    public ContextEntityType getEtype() { return this.etype; }
    public long getInitInterval() { return this.initInterval; }
    public String getRefreshPolicy() { return this.refreshPolicy; }
    public long getRefreshInterval() { return this.refreshInterval; }
    public HashMap<String, String> getParams() { return this.params; }
    public String getContextProvider() { return this.contextProvider; }

    public JobDataMap getJobDataMap() {
        Map<String, Object> jobMap = new HashMap<>();
        jobMap.put("params", (new JSONObject(this.params)).toString()) ;
        jobMap.put("contextId", this.contextId);
        jobMap.put("fetchMode", this.refreshPolicy);
        jobMap.put("contextProvider", this.contextProvider);

        return new JobDataMap(jobMap);
    }

    // Setters
    public void setfthresh(double fthr, double initResiLife) {
        this.fthr = fthr;
        this.refreshInterval = (long) (this.lifetime * 1000 * (1 - fthr));
        this.initInterval = (long) (initResiLife * 1000 * (1 - fthr));
    }
}
