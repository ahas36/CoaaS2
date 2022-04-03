package au.coaas.sqem.util.cacherequest;

import au.coaas.sqem.proto.RegisterEntityRequest;

public class CacheEntityRequest implements CacheRequest {
    private RegisterEntityRequest entity = null;

    public RegisterEntityRequest getEntityRequest() { return entity;}
    public String getEntityId() { return entityId; }
}
