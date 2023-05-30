package com.instantsystem.techtest.parkings.dto;

import java.util.Objects;
import java.util.StringJoiner;

public class ParkingResult extends ParkingInternal implements Comparable<ParkingResult> {

    private double distance;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingResult that = (ParkingResult) o;
        return Double.compare(that.distance, distance) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(distance);
    }

    @Override
    public int compareTo(final ParkingResult other) {
        return Double.compare(this.distance, other.distance);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParkingResult.class.getSimpleName() + "[", "]")
                .add("parking=" + super.toString())
                .add("distance=" + distance)
                .toString();
    }


}
