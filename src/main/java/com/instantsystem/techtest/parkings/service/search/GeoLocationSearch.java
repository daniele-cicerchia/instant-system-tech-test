package com.instantsystem.techtest.parkings.service.search;

import com.github.davidmoten.grumpy.core.Position;
import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Point;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.instantsystem.techtest.parkings.dto.GeoLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of the geolocation search, using an immutable R-Tree implementation.
 * @param <T> a class that extends {@link GeoLocation}
 */
public class GeoLocationSearch<T extends GeoLocation> {

    // Distance computing constants
    private static final double R_2_D = 180.0D / 3.141592653589793D;
    private static final double D_2_R = 3.141592653589793D / 180.0D;
    private static final double D_2_KM = 111189.57696D * R_2_D;


    private RTree<T, Point> rtree;

    /**
     * Load the list of location to search in.
     * @param locations a list of {@link GeoLocation}
     */
    public void loadLocations(final List<T> locations) {

        this.rtree = RTree.star().create();
        locations.forEach(location -> this.rtree = this.rtree.add(location, Geometries.point(location.getLon(), location.getLat())));

    }


    public List<T> getLocations() {
        return this.rtree.entries().toList().toBlocking().single().stream().map(Entry::value).collect(Collectors.toList());
    }


    /**
     * Customised implementation of geo distance computation.
     */
    private double distanceMeters(double lat1, double lon1, double lat2, double lon2) {

        double x = lat1 * D_2_R;
        double y = lat2 * D_2_R;
        return Math.acos( Math.sin(x) * Math.sin(y) + Math.cos(x) * Math.cos(y) * Math.cos(D_2_R * (lon1 - lon2))) * D_2_KM;

    }

    /**
     * Search for all the geopoints in a given distance.
     * @param lat search origin latitude
     * @param lon search origin longitude
     * @param distance max search distance in meters
     * @return a {@link Map} of {@link GeoLocation} points and distances
     */
    public Map<T, Double> search(final double lat, final double lon, final double distance) {

        final Map<T, Double> results = new HashMap<>();

        final Position from = Position.create(lat, lon);


        // Creates an enclosing lat long rectangle for requested distance
        final double distanceKm = distance / 1000;

        Position north = from.predict(distanceKm, 0);
        Position south = from.predict(distanceKm, 180);
        Position east = from.predict(distanceKm, 90);
        Position west = from.predict(distanceKm, 270);
        final Rectangle bounds =  Geometries.rectangle(west.getLon(), south.getLat(), east.getLon(), north.getLat());

        this.rtree.search(bounds) // Does the first search using the bounds
                .filter(entry -> distanceMeters(lat, lon, entry.geometry().y(), entry.geometry().x())  < distance) // Refines using the exact distance
                .toList()
                .toBlocking()
                .getIterator()
                .forEachRemaining(entries -> entries.forEach(entry -> results.put(entry.value(),
                        distanceMeters(lat, lon, entry.geometry().y(), entry.geometry().x())))); // Builds the results list using the referred locations

        return results;

    }


}
