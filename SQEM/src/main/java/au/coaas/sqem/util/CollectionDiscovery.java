package au.coaas.sqem.util;

import au.coaas.cqp.proto.ContextEntityType;

public class CollectionDiscovery {
    // Seems that there exist a Mongo DB collection for each entity type.
    // This is largely in contrast to a common space that a cache would make entities to share.
    public static String discover(ContextEntityType et) {
        String entityType = et.getType();
        switch(entityType){
            case "ParkingFacility":
            case "ParkingGarage":
            case "AutomatedParkingGarage":
            case "BicycleParkingStation":
            case "ParkingLot":
            case "ParkingSpace":
                return "ParkingFacility";
            case "SmartWasteContainer":
                return "WasteContainers";
            case "Vehicle":
            case "BusOrCoach":
            case "Car":
            case "Motorcycle":
            case "MotorizedBicycle":
                return "Vehicle";
            case "Weather":
                return "Weather";
            case "Place":
            case "Accomodation":
            case "AdministrativeArea":
            case "CivicStructure":
            case "Landform":
            case "Residence":
            case "TouristAttraction":
            case "TouristDestination":
                return "Place";
            default:
                return entityType.toLowerCase();
        }
    }
}
