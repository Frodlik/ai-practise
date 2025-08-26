package com.demo.travelbookingapp.controller;

import com.demo.travelbookingapp.dto.FlightDto;
import com.demo.travelbookingapp.dto.FlightSearchRequest;
import com.demo.travelbookingapp.dto.SearchResponse;
import com.demo.travelbookingapp.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Validated
@RequiredArgsConstructor
public class FlightController {
    private final FlightService flightService;

    @GetMapping("/flights")
    public ResponseEntity<SearchResponse<FlightDto>> searchFlights(@Valid FlightSearchRequest request) {
        List<FlightDto> flights = flightService.searchFlights(request);
        SearchResponse<FlightDto> response = new SearchResponse<>(flights);

        return ResponseEntity.ok(response);
    }
}
