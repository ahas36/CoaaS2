package au.coaas.sqem.entity;

import au.coaas.cqp.proto.CdqlConditionToken;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import org.bson.conversions.Bson;

public class ExtendedCdqlConditionToken{

    CdqlConditionToken cdqlConditionToken;
    Bson bson;
    Boolean boolResult;

    public CdqlConditionToken getCdqlConditionToken() {
        return cdqlConditionToken;
    }

    public void setCdqlConditionToken(CdqlConditionToken cdqlConditionToken) {
        this.cdqlConditionToken = cdqlConditionToken;
    }

    public Bson getBson() {
        return bson;
    }

    public void setBson(Bson bson) {
        this.bson = bson;
    }

    public ExtendedCdqlConditionToken(CdqlConditionToken cdqlConditionToken, Bson bson) {
        this.cdqlConditionToken = cdqlConditionToken;
        this.bson = bson;
    }

    public ExtendedCdqlConditionToken(CdqlConditionToken cdqlConditionToken, Boolean bson) {
        this.cdqlConditionToken = cdqlConditionToken;
        this.boolResult = bson;
    }

    public ExtendedCdqlConditionToken(CdqlConditionToken cdqlConditionToken) {
        this.cdqlConditionToken = cdqlConditionToken;
    }
}
