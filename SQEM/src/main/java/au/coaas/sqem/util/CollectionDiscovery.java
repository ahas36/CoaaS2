package au.coaas.sqem.util;

import au.coaas.cqp.proto.ContextEntityType;

/**
 * @author shakthi & ali
 */
public class CollectionDiscovery {
    // Seems that there exist a Mongo DB collection for each entity type.
    // This is largely in contrast to a common space that a cache would make entities to share.
    public static String discover(ContextEntityType et) {
        String entityType = et.getType();
        return discover(entityType);
    }

    public static String discover(String entityType) {
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
            case "Bike":
            case "Motorcycle":
            case "MotorizedBicycle":
                return "Bike";
            case "LinearExtension":
            case "Road":
                return "Road";
            case "Rider":
            case "Driver":
            case "Person":
            case "Girl":
            case "Boy":
            case "Man":
            case "Woman":
                return "Person";
            default:
                return entityType.toLowerCase();
        }
    }
}
