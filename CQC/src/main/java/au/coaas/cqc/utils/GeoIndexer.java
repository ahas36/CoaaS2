package au.coaas.cqc.utils;

import au.coaas.cqc.proto.CordinatesIndex;

import au.coaas.cqc.proto.Path;
import com.uber.h3core.H3Core;
import com.uber.h3core.LengthUnit;
import com.uber.h3core.util.LatLng;

import java.io.IOException;
import java.util.AbstractMap;
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

    // Getter for H3 Address
    public CordinatesIndex getH3Address (double lat, double lng) {
        return getH3Address(lat, lng, getInstance().def_res);
    }

    public CordinatesIndex getH3Address (double lat, double lng, int res) {
        return CordinatesIndex.newBuilder().setLat(lat)
                .setLng(lng).setH3Address(h3Client.latLngToCellAddress(lat, lng, res))
                .build();
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
    public CordinatesIndex getCordinates (String address) {
        LatLng cordinates = h3Client.cellToLatLng(address);
        return CordinatesIndex.newBuilder().setLat(cordinates.lat)
                .setLng(cordinates.lng).setH3Address(address)
                .build();
    }

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

    public int getResolution(String index) {
        return h3Client.getResolution(index);
    }

    // Complete the indexed coordinates object when only address is present.
    public CordinatesIndex getIndexForAddress (CordinatesIndex cords) {
        long index = h3Client.stringToH3(cords.getH3Address());
        return cords.toBuilder().setIndex(index)
                .build();
    }

    // Get all the indices with in the given distance from a given cell.
    public List<Long> getArea (long index, int k) {
        return h3Client.gridDisk(index, k);
    }

    // Returns all the indexes in the path of 2 indexes and the directed edge.
    public Path getPath (long origin, long destination) {
        long edge = h3Client.cellsToDirectedEdge(origin, destination);
        return Path.newBuilder().setOrigin(origin).setDestination(destination)
                .addAllPath(h3Client.gridPathCells(origin, destination))
                .setEdge(edge).setDistace(h3Client.edgeLength(edge, LengthUnit.km))
                .build();
    }

    // Returns the higher level index that the provided index belongs to in the hierachy.
    public long getParentIndex (long index, int targetRes) {
        return h3Client.cellToParent(index, targetRes);
    }

    // Get the predicted path of a mobile entity.
    public Path getPredictedPath (double lat, double lng, double heading, double speed, long time) {
        // Considering speed in Kmph, time in seconds,
        double distance = speed * (time/3600.0);
        AbstractMap.SimpleEntry<Double, Double> destination = estimateDestination (lat, lng, distance, heading);
        return getPath(h3Client.latLngToCell(lat, lng, getInstance().def_res),
                h3Client.latLngToCell(destination.getKey(), destination.getValue(), getInstance().def_res));
    }

    private static final long radius = 6371;
    private AbstractMap.SimpleEntry<Double, Double> estimateDestination (double lat, double lng, double dis, double hed) {
        lat = lat * Math.PI / 180.0;
        lng = lng * Math.PI / 180.0;
        hed = hed * Math.PI / 180.0;

        double lat2 = Math.asin(Math.sin(lat) * Math.cos(dis/radius) +
                Math.cos(lat) * Math.sin(dis/radius) * Math.cos(hed));
        double lon2 = lng + Math.atan2(
                Math.sin(hed) * Math.sin(dis/radius) * Math.cos(lat),
                Math.cos(dis/radius) - Math.sin(lat) * Math.sin(lat2));

        return new AbstractMap.SimpleEntry<>(lat2 * 180 / Math.PI, lon2 * 180 / Math.PI);
    }
}
