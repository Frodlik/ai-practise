package com.demo.travelbookingapp.service;

import com.demo.travelbookingapp.dto.CarDto;
import com.demo.travelbookingapp.dto.CarSearchRequest;
import com.demo.travelbookingapp.provider.CarProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
    private final CarProviderClient carProviderClient;

    public List<CarDto> searchCars(CarSearchRequest request) {
        return carProviderClient.searchCars(request);
    }
}
