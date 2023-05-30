package com.instantsystem.techtest.parkings.dto;

import java.io.Serializable;
import java.util.StringJoiner;

/**
 * Abstract class for a meaningful location on the map.
 */
public abstract class GeoLocation implements Serializable {

    private String id;

    private String name;

    private double lon;

    private double lat;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", GeoLocation.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("name='" + name + "'")
                .add("lon=" + lon)
                .add("lat=" + lat)
                .toString();
    }
}
