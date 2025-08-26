package com.demo.travelbookingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CarDto {
    private String vendor;
    private String carType;
    private String model;
    private BigDecimal price;
    private String currency;
    private String pickupLocation;
    private String dropoffLocation;
    private String pickupDateTime;
    private String dropoffDateTime;
}