package com.demo.travelbookingapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightDto {
    private String airline;
    private String flightNumber;
    private String departureTime;
    private String arrivalTime;
    private String origin;
    private String destination;
    private BigDecimal price;
    private String currency;
    private String duration;
}
