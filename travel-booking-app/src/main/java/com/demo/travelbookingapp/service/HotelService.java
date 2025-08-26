package com.demo.travelbookingapp.service;

import com.demo.travelbookingapp.dto.HotelDto;
import com.demo.travelbookingapp.dto.HotelSearchRequest;
import com.demo.travelbookingapp.provider.HotelProviderClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final HotelProviderClient hotelProviderClient;

    public List<HotelDto> searchHotels(HotelSearchRequest request) {
        return hotelProviderClient.searchHotels(request);
    }
}
