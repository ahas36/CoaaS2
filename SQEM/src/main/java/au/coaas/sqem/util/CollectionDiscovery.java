package au.coaas.sqem.util;

import au.coaas.cqp.proto.ContextEntityType;

public class CollectionDiscovery {

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
