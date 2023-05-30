package com.instantsystem.techtest.parkings.service.data.impl;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;
import com.instantsystem.techtest.parkings.dto.ParkingInternal;
import com.instantsystem.techtest.parkings.dto.ParkingUpdate;
import com.instantsystem.techtest.parkings.service.data.ParkingDataServiceInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PoitiersParkingDataService implements ParkingDataServiceInterface {

    private static final Logger logger = LoggerFactory.getLogger(PoitiersParkingDataService.class);

    @Value("${app.client.parking.details}")
    private String parkingDetailsUrl;

    @Value("${app.client.parking.updates}")
    private String parkingUpdatesUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    private final Gson gson = new Gson();

    @Override
    public List<ParkingInternal> recoverParkingDetails() {

        final List<ParkingInternal> parkingDetails = new ArrayList<>();

        try {
            final String parkingJson = this.restTemplate.getForObject(this.parkingDetailsUrl, String.class);
            final LinkedTreeMap parkingTreeMap = gson.fromJson(parkingJson, LinkedTreeMap.class);

            ((List<LinkedTreeMap>) parkingTreeMap.get("records")).forEach(rec ->
                    parkingDetails.add(this.parseParkingJsonRecordFields((LinkedTreeMap) rec.get("fields"))));


        } catch (final RestClientException exc) {
            logger.error("Error in collecting data from parking details URL", exc);
        } catch (final JsonSyntaxException exc) {
            logger.error("Error in parsing Json from parking details", exc);
        }

        return parkingDetails;

    }


    @Override
    public List<ParkingUpdate> recoverParkingAvailabilityUpdates() {

        final List<ParkingUpdate> updates = new ArrayList<>();

        try {
            final String updatesJson = this.restTemplate.getForObject(this.parkingUpdatesUrl, String.class);
            final LinkedTreeMap updatesTreeMap = gson.fromJson(updatesJson, LinkedTreeMap.class);

            ((List<LinkedTreeMap>) updatesTreeMap.get("records")).forEach(rec ->
                    updates.add(this.parseParkingUpdateJsonFields((LinkedTreeMap) rec.get("fields"))));

        } catch (final RestClientException exc) {
            logger.error("Error in collecting data from parking updates URL", exc);
        } catch (final JsonSyntaxException exc) {
            logger.error("Error in parsing Json from parking updates", exc);
        }

        return updates;

    }


    private ParkingInternal parseParkingJsonRecordFields(final LinkedTreeMap fields) {

        final ParkingInternal result = new ParkingInternal();

        result.setId(fields.get("nom").toString()); // The ID is not used by availability service
        result.setName(this.reformatName(fields.get("nom").toString()));

        final List<Double> geoPoint2d = (List<Double>) fields.get("geo_point_2d");
        result.setLat(geoPoint2d.get(0));
        result.setLon(geoPoint2d.get(1));

        return result;

    }


    private ParkingUpdate parseParkingUpdateJsonFields(final LinkedTreeMap fields) {

        final ParkingUpdate result = new ParkingUpdate();

        result.setId(fields.get("nom").toString());
        result.setCapacity(((Double) fields.get("capacite")).intValue());
        result.setAvailability(((Double) fields.get("places")).intValue());

        return result;

    }


    private String reformatName(final String name) {

        String result = name;

        if (name != null && !name.isEmpty()) {
            result =  Arrays.stream(name.toLowerCase().split("\\s+"))
                    .map(t -> t.substring(0, 1).toUpperCase() + t.substring(1))
                    .collect(Collectors.joining(" "));
        }

        return result;

    }

}
