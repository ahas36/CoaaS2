package au.coaas.cqc.utils;

import au.coaas.cqp.proto.CdqlConditionToken;
import au.coaas.sqem.proto.ContextRequest;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author shakthi
 */
public class ReturnType {
    private List<String> operators;
    private LinkedHashMap<String,String> params;
    private ContextRequest.Builder contextRequest;

    public ReturnType(LinkedHashMap<String,String> params, List<String> operators,
                      ContextRequest.Builder contextRequest) {
        this.operators = operators;
        this.params = params;
        this.contextRequest = contextRequest;
    }

    public List<String> getOperators() {
        return operators;
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }

    public List<CdqlConditionToken> getRpnConditionList() {
        return contextRequest.getCondition().getRPNConditionList();
    }

    public ContextRequest.Builder getContextRequest() {
        return contextRequest;
    }
}
