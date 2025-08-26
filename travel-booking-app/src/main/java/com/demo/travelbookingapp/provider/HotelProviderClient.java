package com.demo.travelbookingapp.provider;

import com.demo.travelbookingapp.dto.HotelDto;
import com.demo.travelbookingapp.dto.HotelSearchRequest;

import java.util.List;

public interface HotelProviderClient {
    List<HotelDto> searchHotels(HotelSearchRequest request);
}
