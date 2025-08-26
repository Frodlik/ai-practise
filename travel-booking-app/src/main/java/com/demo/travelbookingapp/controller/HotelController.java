package com.demo.travelbookingapp.controller;

import com.demo.travelbookingapp.dto.HotelDto;
import com.demo.travelbookingapp.dto.HotelSearchRequest;
import com.demo.travelbookingapp.dto.SearchResponse;
import com.demo.travelbookingapp.service.HotelService;
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
public class HotelController {
    private final HotelService hotelService;

    @GetMapping("/hotels")
    public ResponseEntity<SearchResponse<HotelDto>> searchHotels(@Valid HotelSearchRequest request) {
        List<HotelDto> hotels = hotelService.searchHotels(request);
        SearchResponse<HotelDto> response = new SearchResponse<>(hotels);

        return ResponseEntity.ok(response);
    }
}
