package com.demo.travelbookingapp.provider.amadeus.client;

import com.demo.travelbookingapp.provider.amadeus.dto.FlightOffersRequest;
import com.demo.travelbookingapp.provider.amadeus.dto.FlightOffersResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "amadeus-flights", url = "${amadeus.base-url}")
public interface AmadeusFlightClient {
    @PostMapping(value = "/v2/shopping/flight-offers",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, "application/vnd.amadeus+json"})
    FlightOffersResponse searchFlightOffers(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader(value = "Content-Type", defaultValue = MediaType.APPLICATION_JSON_VALUE) String contentType,
            @RequestBody FlightOffersRequest request
    );
}
