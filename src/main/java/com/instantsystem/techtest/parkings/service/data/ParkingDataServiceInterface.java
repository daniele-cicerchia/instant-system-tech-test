package com.instantsystem.techtest.parkings.service.data;

import com.instantsystem.techtest.parkings.dto.ParkingInternal;
import com.instantsystem.techtest.parkings.dto.ParkingUpdate;

import java.util.List;

@ParkingDataService
public interface ParkingDataServiceInterface {

    List<ParkingInternal> recoverParkingDetails();

    List<ParkingUpdate> recoverParkingAvailabilityUpdates();

}
