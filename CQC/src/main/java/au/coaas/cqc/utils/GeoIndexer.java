package au.coaas.cqc.utils;

import au.coaas.cqc.proto.CordinatesIndex;
import com.uber.h3core.H3Core;
import com.uber.h3core.exceptions.PentagonEncounteredException;
import com.uber.h3core.util.GeoCoord;

import java.io.IOException;
import java.util.List;

public class GeoIndexer {
    private static H3Core h3Client;
    private static GeoIndexer instance;

    // Default resolution
    private final int def_res = 9;

    private GeoIndexer() {
        try {
            h3Client = H3Core.newInstance();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GeoIndexer getInstance() {
        if (instance == null) {
            synchronized (GeoIndexer.class) {
                if (instance == null) {
                    instance = new GeoIndexer();
                }
            }
        }
        return instance;
    }

    // Getter for H3 Address
    public static CordinatesIndex getH3Address (double lat, double lng) {
        return getH3Address(lat, lng, getInstance().def_res);
    }

    public static CordinatesIndex getH3Address (double lat, double lng, int res) {
        return CordinatesIndex.newBuilder().setLat(lat)
                .setLng(lng).setH3Address(h3Client.geoToH3Address(lat, lng, res))
                .build();
    }

    // Getters for H3 Index
    public static CordinatesIndex getGeoIndex (double lat, double lng) {
        return getGeoIndex(lat, lng, getInstance().def_res);
    }

    public static CordinatesIndex getGeoIndex (double lat, double lng, int res) {
        return CordinatesIndex.newBuilder().setLat(lat)
                .setLng(lng).setIndex(h3Client.geoToH3(lat, lng, res))
                .build();
    }

    // Convert an address or an index to c0ordinates
    public static CordinatesIndex getCordinates (String address) {
        GeoCoord cordinates = h3Client.h3ToGeo(address);
        return CordinatesIndex.newBuilder().setLat(cordinates.lat)
                .setLng(cordinates.lng).setH3Address(address)
                .build();
    }

    public static CordinatesIndex getCordinates (long index) {
        GeoCoord cordinates = h3Client.h3ToGeo(index);
        return CordinatesIndex.newBuilder().setLat(cordinates.lat)
                .setLng(cordinates.lng).setIndex(index)
                .build();
    }

    // Get k-ring for a given index
    public static List<Long> getKRing (long index, int k) {
        try {
            return h3Client.hexRing(index, k);
        } catch (PentagonEncounteredException e) {
            e.printStackTrace();
        }
        return null;
    }
}
