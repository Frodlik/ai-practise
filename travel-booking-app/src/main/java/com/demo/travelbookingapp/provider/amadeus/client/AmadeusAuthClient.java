package com.demo.travelbookingapp.provider.amadeus.client;

import com.demo.travelbookingapp.provider.amadeus.dto.AmadeusTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "amadeus-auth", url = "${amadeus.base-url}")
public interface AmadeusAuthClient {
    @PostMapping(value = "/v1/security/oauth2/token",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    AmadeusTokenResponse getToken(@RequestBody Map<String, ?> formParams);
}
