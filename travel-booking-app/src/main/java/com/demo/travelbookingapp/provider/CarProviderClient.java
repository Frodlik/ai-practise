package com.demo.travelbookingapp.provider;

import com.demo.travelbookingapp.dto.CarDto;
import com.demo.travelbookingapp.dto.CarSearchRequest;

import java.util.List;

public interface CarProviderClient {
    List<CarDto> searchCars(CarSearchRequest request);
}
