package com.demo.travelbookingapp.provider.rapidapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PricelineCarsRequest {
    private String pickupCityId;
    private String pickupAirportCode;
    private String dropoffCityId;
    private String dropoffCityString;
    private String pickupDate;
    private String pickupTime;
    private String dropoffDate;
    private String dropoffTime;
    private String driverAge;
    private String currency = "USD";
}