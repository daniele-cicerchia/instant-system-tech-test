package com.instantsystem.techtest.parkings.config;

import com.instantsystem.techtest.parkings.dto.ParkingInternal;
import com.instantsystem.techtest.parkings.service.search.GeoLocationSearch;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class WebApplicationConfig {

    @Bean
    public GeoLocationSearch<ParkingInternal> parkingSearch() {

        return new GeoLocationSearch<>();

    }

}
