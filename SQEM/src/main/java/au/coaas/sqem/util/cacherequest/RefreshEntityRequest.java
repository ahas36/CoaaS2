package au.coaas.sqem.util.cacherequest;

import au.coaas.sqem.proto.RegisterEntityRequest;
import au.coaas.sqem.proto.UpdateEntityRequest;

public class RefreshEntityRequest implements CacheRequest {
    private UpdateEntityRequest entity = null;

    public UpdateEntityRequest getRefreshRequest() { return entity;}
    public String getEntityId() { return entityId; }
}
