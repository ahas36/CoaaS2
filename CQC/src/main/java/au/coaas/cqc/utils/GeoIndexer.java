package au.coaas.cqc.utils;

import au.coaas.cqc.proto.CordinatesIndex;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.GeoCoord;
import com.uber.h3core.exceptions.LineUndefinedException;
import com.uber.h3core.exceptions.PentagonEncounteredException;

import java.io.IOException;
import java.util.ArrayList;
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

    // Convert an address or an index to coordinates.
    // But this coordinate is the center of the index cell.
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

    // Inspection functions.
    // Get the current resolution of an index. Useful when changing between resolutions.
    public static int getResolution(long index) {
        return h3Client.h3GetResolution(index);
    }

    public static int getResolution(String index) {
        return h3Client.h3GetResolution(index);
    }

    // Complete the indexed coordinates object when only address is present.
    public static CordinatesIndex getIndexForAddress (CordinatesIndex cords) {
        long index = h3Client.stringToH3(cords.getH3Address());
        return cords.toBuilder().setIndex(index)
                .build();
    }

    // Get all the indices k-levels around a given cell. The output is a hollow circle.
    public static List<Long> getKRing (long index, int k) {
        try {
            return h3Client.hexRing(index, k);
        } catch (PentagonEncounteredException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Get all the indices with in the given distance from a given cell.
    public static List<Long> getKRange (long index, int k) {
        try {
            List<Long> kRange = new ArrayList<>();
            List<List<Long>> indexesList = h3Client.hexRange(index, k);
            for(List<Long> indexes : indexesList) {
                for(long idx : indexes) {
                    kRange.add(idx);
                }
            }
            return kRange;
        } catch (PentagonEncounteredException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Returns the indexed line between 2 indexes of the same resolution.
    public static List<Long> getLine (long indexA, long indexB) {
        try {
            return h3Client.h3Line(indexA, indexB);
        } catch (LineUndefinedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static long getEdge (long index1, long index2) {
        return h3Client.getH3UnidirectionalEdge(index1, index2);
    }

    // Returns the higher level index that the provided index belongs to in the hierachy.
    public static long getParentIndex (long index, int targetRes) {
        return h3Client.h3ToParent(index, targetRes);
    }
}
