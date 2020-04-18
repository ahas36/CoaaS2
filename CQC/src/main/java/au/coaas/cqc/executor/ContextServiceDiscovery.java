package au.coaas.cqc.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class ContextServiceDiscovery {

    private static Logger log = Logger.getLogger(ContextServiceDiscovery.class.getName());

    private final static String BASE_URI = "https://coaas.csiro.au/";

    public static String discover(String type, Set<String> keys) {
        List<String> candiadteSet = new ArrayList<>();
        switch (type.toLowerCase()) {
            case "place":
            case "schema:place":
            case "http://schema.org:place":
                candiadteSet.add(BASE_URI + "GoogleGeocodingContextProvider/rest/google/geocoder/{address}");
                candiadteSet.add(BASE_URI + "GoogleGeocodingContextProvider/rest/google/geocoder/Reverse/{geo.latitude}/{geo.longitude}");
                break;
            case "event":
            case "schema:event":
            case "http://schema.org:event":
                candiadteSet.add("https://coaas.csiro.au/GoogleCalendarContextProvider/rest/google/calendar/events/{attendee.email}");
//                candiadteSet.add("http://138.194.106.29/GoogleCalendarContextProvider/rest/google/calendar/events/{email}/{start}");
                break;
            case "weather":
            case "schema:weather":
            case "http://schema.org:weather":
                return BASE_URI + "WeatherContextProvider/rest/weatherreport/{location.latitude}/{location.longitude}";
            case "car":
            case "schema:car":
            case "http://schema.org:car":
                return BASE_URI + "CarSpecContextProvider/rest/carspec/{manufacturer}/{model}/{vehicleModelDate}";
            case "carpark":
            case "mv:carpark":
            case "http://mobivoc.org:carpark":
            case "ParkingGarage":
            case "mv:ParkingGarage":
            case "ParkingSpace":
            case "mv:ParkingSpace":
            case "http://mobivoc.org:ParkingGarage":
            case "http://mobivoc.org:ParkingSpace": {
                return BASE_URI + "sqem/rest/query/";
            }
        }
        String selectedURI = null;
        int maxMatch = 0;
        for (String candidateURI : candiadteSet) {
            int currentMatch = 0;
            for (String key : keys) {
                if (candidateURI.contains(key)) {
                    currentMatch++;
                }
            }
            if (currentMatch > maxMatch) {
                selectedURI = candidateURI;
                maxMatch = currentMatch;
            }
        }
        return selectedURI;
    }
}
