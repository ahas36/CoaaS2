package au.coaas.cqc.utils.exceptions;

import au.coaas.cqp.proto.CdqlConditionToken;
import org.json.JSONObject;

public class ExtendedToken {
    private CdqlConditionToken cdqlConditionToken;
    private Object data;

    public CdqlConditionToken getCdqlConditionToken() {
        return cdqlConditionToken;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ExtendedToken(CdqlConditionToken cdqlConditionToken, Object data) {
        this.cdqlConditionToken = cdqlConditionToken;
        this.data = data;
    }

    public ExtendedToken(CdqlConditionToken cdqlConditionToken) {
        this.cdqlConditionToken = cdqlConditionToken;
    }

}
