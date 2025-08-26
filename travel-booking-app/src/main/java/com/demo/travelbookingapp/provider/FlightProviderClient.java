package com.demo.travelbookingapp.provider;

import com.demo.travelbookingapp.dto.FlightDto;
import com.demo.travelbookingapp.dto.FlightSearchRequest;

import java.util.List;

public interface FlightProviderClient {
    List<FlightDto> searchFlights(FlightSearchRequest request);
}
