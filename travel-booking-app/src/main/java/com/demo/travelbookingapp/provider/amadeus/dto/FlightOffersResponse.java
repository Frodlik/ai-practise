package com.demo.travelbookingapp.provider.amadeus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlightOffersResponse {
    private List<FlightOffer> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class FlightOffer {
        private String type;
        private String id;
        private List<Itinerary> itineraries;
        private Price price;
        private String validatingAirlineCodes;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Itinerary {
        private String duration;
        private List<Segment> segments;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Segment {
        private Departure departure;
        private Arrival arrival;
        private String carrierCode;
        private String number;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Departure {
        private String iataCode;
        private String at;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Arrival {
        private String iataCode;
        private String at;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Price {
        private String currency;
        private BigDecimal total;
    }
}
