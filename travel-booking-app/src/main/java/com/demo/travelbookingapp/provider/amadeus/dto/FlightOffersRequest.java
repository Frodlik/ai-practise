package com.demo.travelbookingapp.provider.amadeus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightOffersRequest {
    private String currencyCode = "USD";
    private List<OriginDestination> originDestinations;
    private List<Traveler> travelers;
    private List<String> sources = List.of("GDS");

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OriginDestination {
        private String id;
        private String originLocationCode;
        private String destinationLocationCode;
        private DepartureDateTimeRange departureDateTimeRange;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepartureDateTimeRange {
        private String date;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Traveler {
        private String id;
        private String travelerType = "ADULT";

        public Traveler(String id) {
            this.id = id;
        }
    }
}
