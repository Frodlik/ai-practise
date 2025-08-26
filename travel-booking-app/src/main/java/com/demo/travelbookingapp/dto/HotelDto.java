package com.demo.travelbookingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelDto {
    private String hotelName;
    private String address;
    private BigDecimal price;
    private String currency;
    private String checkIn;
    private String checkOut;
    private Double rating;
    private String amenities;
}
