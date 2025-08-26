package com.demo.travelbookingapp.provider.amadeus.service;

import com.demo.travelbookingapp.provider.amadeus.client.AmadeusAuthClient;
import com.demo.travelbookingapp.provider.amadeus.dto.AmadeusTokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AmadeusTokenService {
    private final AmadeusAuthClient authClient;

    @Value("${amadeus.client-id}")
    private String clientId;

    @Value("${amadeus.client-secret}")
    private String clientSecret;

    public String getAccessToken() {
        Map<String, String> formParams = new HashMap<>();
        formParams.put("grant_type", "client_credentials");
        formParams.put("client_id", clientId);
        formParams.put("client_secret", clientSecret);

        AmadeusTokenResponse tokenResponse = authClient.getToken(formParams);
        return "Bearer " + tokenResponse.getAccessToken();
    }
}
