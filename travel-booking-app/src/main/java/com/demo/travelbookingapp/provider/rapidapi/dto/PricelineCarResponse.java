package com.demo.travelbookingapp.provider.rapidapi.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class PricelineCarResponse {
    private Results results;

    @Data
    public static class Results {
        private List<Car> cars;
    }

    @Data
    public static class Car {
        private Vehicle vehicle;
        private Rate rate;
    }

    @Data
    public static class Vehicle {
        private String brand;
        private String model;
        private String example;
    }

    @Data
    public static class Rate {
        private BigDecimal totalPrice;
        private String currencyCode;
    }
}

