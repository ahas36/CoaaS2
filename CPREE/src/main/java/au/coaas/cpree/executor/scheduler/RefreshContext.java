package au.coaas.cpree.executor.scheduler;

import au.coaas.cqp.proto.ContextEntityType;
import au.coaas.grpc.client.SQEMChannel;
import au.coaas.sqem.proto.ContextServiceId;
import au.coaas.sqem.proto.SQEMResponse;
import au.coaas.sqem.proto.SQEMServiceGrpc;
import org.json.JSONObject;
import org.quartz.JobDataMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RefreshContext {
    private double fthr;
    private String csId;
    private double lifetime;
    private String contextId;
    private String refreshPolicy;
    private String contextProvider;
    private ContextEntityType etype;
    private Map<String, String> params;
    private double currentSamplingInt;

    private long initInterval;
    private long refreshInterval; // This is in miliseconds

    // Refresh Interval is specific to the next retrieval only because,
    // refInteral = Lifetime - age - retrievalLatency
    public RefreshContext(String contextId, double fthr, String refreshPolicy, double initResiLife,
                          double lifetime, Map<String, String> params, String contextProvider,
                          ContextEntityType et){
        this.etype = et;
        this.fthr = fthr;
        this.params = params;
        this.lifetime = lifetime;
        this.contextId = contextId;
        this.refreshPolicy = refreshPolicy;

        JSONObject cpObj = new JSONObject(contextProvider);
        this.csId = cpObj.getJSONObject("_id").getString("$oid");
        double samplingInt = cpObj.getJSONObject("sla").getJSONObject("updateFrequency").getDouble("value");

        this.currentSamplingInt = samplingInt;
        cpObj.getJSONObject("sla").getJSONObject("freshness").put("fthresh", this.fthr);
        this.contextProvider = cpObj.toString();

        // The parameter, when passed, should be set in seconds.
        if(initResiLife < 0) initResiLife = 0;
        double adjustment = lifetime - initResiLife;
        if(lifetime > samplingInt){
            this.refreshInterval = (long) (samplingInt + ((lifetime - samplingInt) * (1.0 - fthr))) * 1000;
            if(adjustment < samplingInt) {
                // If the retrieval latency + age loss is less than the sampling interval.
                double diff = samplingInt - adjustment;
                this.initInterval = (long) (diff + ((initResiLife - samplingInt) * (1.0 - fthr))) * 1000;
            }
            else {
                // If the retrieval latency + age loss is greater than the sampling interval.
                double diff = adjustment - samplingInt;
                double exp_prd = (lifetime - samplingInt) * (1.0 - fthr);
                // Checking if the exp_prd has already elapsed that the item need immediatly be refreshed.
                if(exp_prd < diff) this.initInterval = 0;
                else this.initInterval = (long) (exp_prd - diff) * 1000;
            }
        }
        else {
            this.refreshInterval = (long) samplingInt * 1000;
            this.initInterval = (long) initResiLife * 1000;
        }

    }

    // Getters
    public double getfthresh() { return this.fthr; }
    public String getContextId(){ return this.contextId; }
    public ContextEntityType getEtype() { return this.etype; }
    public long getInitInterval() { return this.initInterval; }
    public String getRefreshPolicy() { return this.refreshPolicy; }
    public long getRefreshInterval() { return this.refreshInterval; }
    public Map<String, String> getParams() { return this.params; }
    public String getContextProvider() { return this.contextProvider; }

    public JobDataMap getJobDataMap() {
        Map<String, Object> jobMap = new HashMap<>();

        jobMap.put("cpId", this.csId);
        jobMap.put("contextId", this.contextId);
        jobMap.put("fetchMode", this.refreshPolicy);
        jobMap.put("entityType", this.etype.getType());
        jobMap.put("contextProvider", this.contextProvider);
        jobMap.put("params", (new JSONObject(this.params)).toString());

        return new JobDataMap(jobMap);
    }

    // Setters
    public void setfthresh(double fthr, double initResiLife) {
        this.fthr = fthr;

        if(initResiLife < 0) initResiLife = 0;
        double adjustment = lifetime - initResiLife;
        if(lifetime > currentSamplingInt){
            this.refreshInterval = (long) (currentSamplingInt + ((lifetime - currentSamplingInt) * (1.0 - fthr))) * 1000;
            if(adjustment < currentSamplingInt) {
                // If the retrieval latency + age loss is less than the sampling interval.
                double diff = currentSamplingInt - adjustment;
                this.initInterval = (long) (diff + ((initResiLife - currentSamplingInt) * (1.0 - fthr))) * 1000;
            }
            else {
                // If the retrieval latency + age loss is greater than the sampling interval.
                double diff = adjustment - currentSamplingInt;
                double exp_prd = (lifetime - currentSamplingInt) * (1.0 - fthr);
                // Checking if the exp_prd has already elapsed that the item need immediatly be refreshed.
                if(exp_prd < diff) this.initInterval = 0;
                else this.initInterval = (long) (exp_prd - diff) * 1000;
            }
        }
        else {
            this.refreshInterval = (long) currentSamplingInt * 1000;
            this.initInterval = (long) initResiLife * 1000;
        }
    }

    public void setInitInterval(double initResiLife, String cpId){
        // This can be wrong now (the initInterval)
        if(!cpId.equals(csId)){
            this.csId = cpId;
            SQEMServiceGrpc.SQEMServiceBlockingStub sqemStub =
                    SQEMServiceGrpc.newBlockingStub(SQEMChannel.getInstance().getChannel());
            SQEMResponse csResponse = sqemStub.getContextServiceInfo(ContextServiceId
                    .newBuilder().setId(cpId).build());
            this.contextProvider = csResponse.getBody();
        }

        if(initResiLife < 0) initResiLife = 0;
        double adjustment = lifetime - initResiLife;
        if(lifetime > this.currentSamplingInt){
            this.refreshInterval = (long) (currentSamplingInt + ((lifetime - currentSamplingInt) * (1.0 - fthr))) * 1000;
            if(adjustment < currentSamplingInt) {
                // If the retrieval latency + age loss is less than the sampling interval.
                double diff = currentSamplingInt - adjustment;
                this.initInterval = (long) (diff + ((initResiLife - currentSamplingInt) * (1.0 - fthr))) * 1000;
            }
            else {
                // If the retrieval latency + age loss is greater than the sampling interval.
                double diff = adjustment - currentSamplingInt;
                double exp_prd = (lifetime - currentSamplingInt) * (1.0 - fthr);
                // Checking if the exp_prd has already elapsed that the item need immediatly be refreshed.
                if(exp_prd < diff) this.initInterval = 0;
                else this.initInterval = (long) (exp_prd - diff) * 1000;
            }
        }
        else {
            this.refreshInterval = (long) currentSamplingInt * 1000;
            this.initInterval = (long) initResiLife * 1000;
        }
    }
}
