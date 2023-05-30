package com.instantsystem.techtest.parkings;

import com.instantsystem.techtest.parkings.dto.Parking;
import com.instantsystem.techtest.parkings.service.ParkingSearchService;
import com.instantsystem.techtest.parkings.web.ParkingSearchController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest
class ParkingsApplicationTests {

    private ParkingSearchController controller;

    @MockBean
    private ParkingSearchService parkingSearchService;

    @Autowired
    public void setController(ParkingSearchController controller) {
        this.controller = controller;
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(this.controller);
    }

    @Test
    void controllerTest() {

        final double lat = 1.0;
        final double lon = 1.0;
        final double distance = 100.0;
        final boolean allResults = false;

        when(parkingSearchService.searchForParking(lat, lon, distance, allResults)).thenReturn(new ArrayList<>());

        final ResponseEntity<List<Parking>> response = this.controller.searchForParking(lat, lon, distance, allResults);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(0, response.getBody().size());

    }

}
