package com.demo.travelbookingapp.provider.amadeus.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelOffersResponse {
    private List<HotelOffer> data;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HotelOffer {
        private String type;
        private Hotel hotel;
        private List<Offer> offers;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Hotel {
        private String type;
        private String hotelId;
        private String chainCode;
        private String dupeId;
        private String name;
        private String cityCode;
        private Double latitude;
        private Double longitude;
        private Address address;
        private Rating rating;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private List<String> lines;
        private String postalCode;
        private String cityName;
        private String countryCode;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Rating {
        private Double rating;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Offer {
        private String id;
        private String checkInDate;
        private String checkOutDate;
        private Price price;
        private Room room;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Price {
        private String currency;
        private BigDecimal total;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Room {
        private String type;
        private String typeEstimated;
        private Description description;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Description {
        private String text;
        private String lang;
    }
}
