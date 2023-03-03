package au.coaas.cqc.executor;

import au.coaas.cqc.proto.CdqlResponse;
import au.coaas.cqc.proto.EventRequest;

import java.util.logging.Logger;

public class SituationManager {
    private static Logger log = Logger.getLogger(SituationManager.class.getName());

    public static CdqlResponse handleEvent(EventRequest event){
        CdqlResponse cdqlResponse = CdqlResponse.newBuilder().setStatus("200").build();
        return cdqlResponse;
    }
}
