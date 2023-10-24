package au.coaas.cqc.utils;

import au.coaas.cqp.proto.CdqlConditionToken;

import java.util.LinkedHashMap;
import java.util.List;

public class ReturnType {
    private List<String> operators;
    private LinkedHashMap<String,String> params;
    private List<CdqlConditionToken> rpnConditionList;

    public ReturnType(LinkedHashMap<String,String> params, List<String> operators,
                      List<CdqlConditionToken> rpnConditionList) {
        this.operators = operators;
        this.params = params;
        this.rpnConditionList = rpnConditionList;
    }

    public List<String> getOperators() {
        return operators;
    }

    public LinkedHashMap<String, String> getParams() {
        return params;
    }

    public List<CdqlConditionToken> getRpnConditionList() {
        return rpnConditionList;
    }
}
