package com.instantsystem.techtest.parkings.service;

import com.instantsystem.techtest.parkings.dto.ParkingInternal;
import com.instantsystem.techtest.parkings.dto.ParkingResult;
import com.instantsystem.techtest.parkings.dto.ParkingUpdate;
import com.instantsystem.techtest.parkings.service.data.ParkingDataServiceInterface;
import com.instantsystem.techtest.parkings.service.search.GeoLocationSearch;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Service
public class ParkingSearchService {

    private static final Logger logger = LoggerFactory.getLogger(ParkingSearchService.class);

    @Value("#{T(Double).parseDouble('${app.search.defaultMaxDistance}')}")
    private Double defaultMaxDistance;

    // Locks for concurrency management
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    private final ModelMapper modelMapper = new ModelMapper();

    private GeoLocationSearch<ParkingInternal> parkingSearch;

    private ParkingDataServiceInterface parkingDataService;

    @Autowired
    public void setParkingSearch(GeoLocationSearch<ParkingInternal> parkingSearch) {
        this.parkingSearch = parkingSearch;
    }

    @Autowired
    public void setParkingDataService(ParkingDataServiceInterface parkingDataServiceInterface) {
        this.parkingDataService = parkingDataServiceInterface;
    }

    @PostConstruct
    @Scheduled(cron = "${app.client.scheduling.detailsCron}")
    public void parkingDetails() {

        logger.info("Start parkingDetails");
        this.writeLock.lock();
        final List<ParkingInternal> parkingList = this.parkingDataService.recoverParkingDetails();
        logger.info("Parking details found: {}", parkingList.size());

        this.parkingSearch.loadLocations(parkingList);

        this.writeLock.unlock();

        logger.info("Update parking availabilities");
        this.parkingAvailabilityUpdates();

        logger.info("End parkingDetails");

    }


    @Scheduled(fixedDelayString = "${app.client.scheduling.updatesDelay}", initialDelayString = "${app.client.scheduling.updatesDelay}")
    public void parkingAvailabilityUpdates() {

        logger.info("Start parkingAvailabilityUpdates");
        this.writeLock.lock();
        final List<ParkingUpdate> parkingUpdates = this.parkingDataService.recoverParkingAvailabilityUpdates();
        logger.info("Parking availability updates found: {}", parkingUpdates.size());

        /*
         * R-Tree implementation used is immutable, so the parking details must be extracted and then loaded again
         * in the GeoLocationSearch service.
         */
        final Map<String, ParkingInternal> parkings = new HashMap<>();
        this.parkingSearch.getLocations().forEach(parking -> parkings.put(parking.getId(), parking));

        parkingUpdates.forEach(parkingUpdate -> {
            final ParkingInternal parking = parkings.get(parkingUpdate.getId());
            if (parking != null) {
                parking.setCapacity(parkingUpdate.getCapacity());
                parking.setAvailability(parkingUpdate.getAvailability());
            } else {
                logger.warn("Cannot find parking with id: {}", parkingUpdate.getId());
            }
        });

        this.parkingSearch.loadLocations(new ArrayList<>(parkings.values()));

        this.writeLock.unlock();
        logger.info("End parkingAvailabilityUpdates");

    }


    public List<ParkingResult> searchForParking(final Double lat, final Double lon, final Double maxDistance, final Boolean allResults) {

        logger.info("Start searchForParking({}, {}, {})", lat, lon, maxDistance);
        this.readLock.lock();

        final Map<ParkingInternal, Double> results = this.parkingSearch.search(lat, lon, maxDistance != null ? maxDistance : this.defaultMaxDistance);
        logger.debug("Found locations: {}", results.keySet().size());

        final List<ParkingResult> result = results
                .keySet()
                .stream()
                .filter(parking -> allResults || parking.getAvailability() != null && parking.getAvailability() > 0)
                .map(parking -> {
                    final ParkingResult parkingResult = this.modelMapper.map(parking, ParkingResult.class);
                    parkingResult.setDistance(results.get(parking));
                    return parkingResult;
                })
                .sorted() // ParkingResult is comparable, using distance
                .collect(Collectors.toList());

        this.readLock.unlock();
        logger.debug("Available parkings: {}", result.size());
        logger.info("End searchForParking");

        return result;

    }


}
