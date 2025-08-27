package com.demo.travelbookingapp.provider.rapidapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class PricelineLocationResponse {
    private List<Location> locations;

    @Data
    public static class Location {
        private String id;
        private String name;
        private String type;
        private String city;
        private String country;
        private String iata;
    }
}
