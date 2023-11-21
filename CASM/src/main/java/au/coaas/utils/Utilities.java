package au.coaas.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.exception.GeoIp2Exception;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

public class Utilities {
    // Comment following when deploying.
    private static final String dbLocation = "/target/lib/geolite2-city.mmdb";
    // Uncomment the following when deploying.
    // private static final String dbLocation = "/opt/payara/deployments/geolite2-city.mmdb";

    public static String convertIpToLocation (String ip) throws IOException, GeoIp2Exception {
        File database = new File(dbLocation);
        DatabaseReader dbReader = new DatabaseReader.Builder(database).build();

        InetAddress ipAddress = InetAddress.getByName(ip);
        CityResponse response = dbReader.city(ipAddress);

        String latitude = response.getLocation().getLatitude().toString();
        String longitude = response.getLocation().getLongitude().toString();

        return latitude + ";" + longitude;
    }
}
