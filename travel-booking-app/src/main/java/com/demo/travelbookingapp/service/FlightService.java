package com.demo.travelbookingapp.service;

import com.demo.travelbookingapp.dto.FlightDto;
import com.demo.travelbookingapp.dto.FlightSearchRequest;
import com.demo.travelbookingapp.provider.FlightProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightProviderClient flightProviderClient;

    public List<FlightDto> searchFlights(FlightSearchRequest request) {
        return flightProviderClient.searchFlights(request);
    }
}
