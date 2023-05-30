package com.instantsystem.techtest.parkings.dto;

import java.io.Serializable;
import java.util.StringJoiner;

public class ParkingUpdate implements Serializable {

    private String id;

    private Integer capacity;

    private Integer availability;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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
        return new StringJoiner(", ", ParkingUpdate.class.getSimpleName() + "[", "]")
                .add("id='" + id + "'")
                .add("capacity=" + capacity)
                .add("availability=" + availability)
                .toString();
    }

}
