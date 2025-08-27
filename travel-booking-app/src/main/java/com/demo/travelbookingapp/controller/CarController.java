package com.demo.travelbookingapp.controller;

import com.demo.travelbookingapp.dto.CarDto;
import com.demo.travelbookingapp.dto.CarSearchRequest;
import com.demo.travelbookingapp.dto.SearchResponse;
import com.demo.travelbookingapp.service.CarService;
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
public class CarController {
    private final CarService carService;

    @GetMapping("/cars")
    public ResponseEntity<SearchResponse<CarDto>> searchCars(@Valid CarSearchRequest request) {
        List<CarDto> cars = carService.searchCars(request);
        SearchResponse<CarDto> response = new SearchResponse<>(cars);

        return ResponseEntity.ok(response);
    }
}