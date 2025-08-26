package com.demo.travelbookingapp.controller;

import com.demo.travelbookingapp.dto.CarDto;
import com.demo.travelbookingapp.dto.CarSearchRequest;
import com.demo.travelbookingapp.dto.SearchResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("/api/v1")
@Validated
public class CarController {
    @GetMapping("/cars")
    public ResponseEntity<SearchResponse<CarDto>> searchCars(@Valid CarSearchRequest request) {
        SearchResponse<CarDto> response = new SearchResponse<>(Collections.emptyList());

        return ResponseEntity.ok(response);
    }
}