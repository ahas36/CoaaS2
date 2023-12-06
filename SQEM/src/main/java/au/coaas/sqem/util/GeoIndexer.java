package au.coaas.sqem.util;

import au.coaas.cqc.proto.CordinatesIndex;

import com.uber.h3core.H3Core;
import com.uber.h3core.util.LatLng;

import java.io.IOException;
import java.util.List;

public class GeoIndexer {
    private static H3Core h3Client;
    private static GeoIndexer instance;

    // Default resolution = 10 because it is both coarse and fine enough to
    // capture the area that a rider, or a vehicle would be in.
    private final int def_res = 10;

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

    // Getters for H3 Index
    public CordinatesIndex getGeoIndex (double lat, double lng) {
        return getGeoIndex(lat, lng, getInstance().def_res);
    }

    public CordinatesIndex getGeoIndex (double lat, double lng, int res) {
        return CordinatesIndex.newBuilder().setLat(lat)
                .setLng(lng).setIndex(h3Client.latLngToCell(lat, lng, res))
                .build();
    }

    // Convert an address or an index to coordinates.
    // But this coordinate is the center of the index cell.
    public CordinatesIndex getCordinates (long index) {
        LatLng cordinates = h3Client.cellToLatLng(index);
        return CordinatesIndex.newBuilder().setLat(cordinates.lat)
                .setLng(cordinates.lng).setIndex(index)
                .build();
    }

    // Inspection functions.
    // Get the current resolution of an index. Useful when changing between resolutions.
    public int getResolution(long index) {
        return h3Client.getResolution(index);
    }

    // Get all the indices with in the given distance from a given cell.
    public List<Long> getArea (long index, int k) {
        return h3Client.gridDisk(index, k);
    }

    // Returns the higher level index that the provided index belongs to in the hierachy.
    public long getParentIndex (long index, int targetRes) {
        return h3Client.cellToParent(index, targetRes);
    }

    public boolean isParent (long index, long pos_parent) {
        int res = getResolution(index);
        List<Long> children = h3Client.cellToChildren(pos_parent, res);
        return children.contains(index);
    }

    public long distance (long origin_idx, long des_idx) {
        int origin_res = h3Client.getResolution(origin_idx);
        int des_res = h3Client.getResolution(des_idx);
        if(origin_res != des_res) des_idx = h3Client.cellToCenterChild(des_idx, origin_res);
        return h3Client.gridDistance(origin_idx, des_idx);
    }
}
