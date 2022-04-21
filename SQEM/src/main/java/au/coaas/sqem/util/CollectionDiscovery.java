package au.coaas.sqem.util;

import au.coaas.cqp.proto.ContextEntityType;

public class CollectionDiscovery {
    // Seems that there exist a Mongo DB collection for each entity type.
    // This is largely in contrast to the a common space that a cache would make entities to share.
    public static String discover(ContextEntityType et) {
        String entityType = et.getType();
        if ("ParkingFacility".equals(entityType)
                || "ParkingGarage".equals(entityType)
                || "AutomatedParkingGarage".equals(entityType)
                || "BicycleParkingStation".equals(entityType)
                || "ParkingLot".equals(entityType)
                || "ParkingSpace".equals(entityType)
                || "UndergroundParkingGarage".equals(entityType)) {
            return ("ParkingFacility");
        } else if ("SmartWasteContainer".equals(entityType)) {
            return ("WasteContainers");
        } else {
            return (entityType.toLowerCase());
        }
    }
}
