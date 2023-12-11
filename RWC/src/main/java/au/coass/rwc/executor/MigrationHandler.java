package au.coass.rwc.executor;

import au.coaas.rwc.proto.MigrationRequest;
import au.coaas.rwc.proto.RWCResponse;

public class MigrationHandler {
    public static RWCResponse migrate(MigrationRequest request) {
        // Step 1. Migrate the last context about the entity in interest to the destination.
        // Step 2. Start the scheduler service in the destination.
        // Step 3. Stop the current scheduler from running locally.
        // Step 4. Change the subscription at the Master.
        // Step 5. Persist the historical context about this entity in the master.

        return RWCResponse.newBuilder().setStatus("200").build();
    }
}
