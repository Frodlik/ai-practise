package com.demo.travelbookingapp.provider.rapidapi.client;

import com.demo.travelbookingapp.provider.rapidapi.dto.PricelineCarResponse;
import com.demo.travelbookingapp.provider.rapidapi.dto.PricelineLocationResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "priceline-cars-api", url = "${car-api.base-url:https://priceline-com-provider.p.rapidapi.com}")
public interface PricelineCarClient {

    @GetMapping("/v1/cars-rentals/locations")
    List<PricelineLocationResponse.Location> searchLocations(
            @RequestHeader("X-RapidAPI-Key") String apiKey,
            @RequestHeader("X-RapidAPI-Host") String apiHost,
            @RequestParam("name") String locationName
    );

    @GetMapping("/v2/cars/resultsRequest")
    PricelineCarResponse searchCars(
            @RequestHeader("X-RapidAPI-Key") String apiKey,
            @RequestHeader("X-RapidAPI-Host") String apiHost,
            @RequestParam("pickup_date") String pickupDate,
            @RequestParam("pickup_time") String pickupTime,
            @RequestParam("dropoff_date") String dropoffDate,
            @RequestParam("dropoff_time") String dropoffTime,
            @RequestParam(value = "pickup_city_id", required = false) String pickupCityId,
            @RequestParam(value = "dropoff_city_id", required = false) String dropoffCityId,
            @RequestParam(value = "pickup_airport_code", required = false) String pickupAirportCode,
            @RequestParam(value = "dropoff_city_string", required = false) String dropoffCityString
    );
}
