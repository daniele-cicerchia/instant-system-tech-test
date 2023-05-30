package com.instantsystem.techtest.parkings.dto;


import java.util.StringJoiner;

public class ParkingInternal extends GeoLocation {

    private Integer capacity;

    private Integer availability;

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getAvailability() {
        return availability;
    }

    public void setAvailability(Integer availability) {
        this.availability = availability;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ParkingInternal.class.getSimpleName() + "[", "]")
                .add("location=" + super.toString())
                .add("capacity=" + capacity)
                .add("availability=" + availability)
                .toString();
    }
}
