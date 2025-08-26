package com.demo.travelbookingapp.provider.amadeus.client;

import com.demo.travelbookingapp.provider.amadeus.dto.HotelOffersResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "amadeus-hotels", url = "${amadeus.base-url}")
public interface AmadeusHotelClient {

    @GetMapping(value = "/v3/shopping/hotel-offers",
            produces = {MediaType.APPLICATION_JSON_VALUE, "application/vnd.amadeus+json"})
    HotelOffersResponse searchHotelOffers(
            @RequestHeader("Authorization") String authorization,
            @RequestParam("cityCode") String cityCode,
            @RequestParam("adults") int adults,
            @RequestParam("checkInDate") String checkInDate,
            @RequestParam("checkOutDate") String checkOutDate
    );
}
