package com.instantsystem.techtest.parkings.web;

import com.instantsystem.techtest.parkings.dto.Parking;
import com.instantsystem.techtest.parkings.service.ParkingSearchService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ParkingSearchController implements ParkingApi {

    private ParkingSearchService parkingSearchService;

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    public void setParkingSearchService(ParkingSearchService parkingSearchService) {
        this.parkingSearchService = parkingSearchService;
    }

    @Override
    public ResponseEntity<List<Parking>> searchForParking(final Double lat,
                                                          final Double lon,
                                                          final Double maxDistance,
                                                          final Boolean allResults) {

        final List<Parking> result = this.parkingSearchService.searchForParking(lat, lon, maxDistance, allResults)
                .stream()
                .map(parking -> this.modelMapper.map(parking, Parking.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);

    }

}
