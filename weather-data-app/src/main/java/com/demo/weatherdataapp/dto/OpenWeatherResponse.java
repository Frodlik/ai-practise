package com.demo.weatherdataapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class OpenWeatherResponse {
    @JsonProperty("coord")
    private Coordinates coord;

    @JsonProperty("weather")
    private List<Weather> weather;

    @JsonProperty("base")
    private String base;

    @JsonProperty("main")
    private Main main;

    @JsonProperty("visibility")
    private Integer visibility;

    @JsonProperty("wind")
    private Wind wind;

    @JsonProperty("clouds")
    private Clouds clouds;

    @JsonProperty("dt")
    private Long dt;

    @JsonProperty("sys")
    private Sys sys;

    @JsonProperty("timezone")
    private Integer timezone;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("cod")
    private Integer cod;

    @Data
    public static class Coordinates {
        @JsonProperty("lon")
        private Double lon;
        @JsonProperty("lat")
        private Double lat;
    }

    @Data
    public static class Weather {
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("main")
        private String main;
        @JsonProperty("description")
        private String description;
        @JsonProperty("icon")
        private String icon;
    }

    @Data
    public static class Main {
        @JsonProperty("temp")
        private Double temp;
        @JsonProperty("feels_like")
        private Double feelsLike;
        @JsonProperty("temp_min")
        private Double tempMin;
        @JsonProperty("temp_max")
        private Double tempMax;
        @JsonProperty("pressure")
        private Integer pressure;
        @JsonProperty("humidity")
        private Integer humidity;
    }

    @Data
    public static class Wind {
        @JsonProperty("speed")
        private Double speed;
        @JsonProperty("deg")
        private Integer deg;
    }

    @Data
    public static class Clouds {
        @JsonProperty("all")
        private Integer all;
    }

    @Data
    public static class Sys {
        @JsonProperty("type")
        private Integer type;
        @JsonProperty("id")
        private Integer id;
        @JsonProperty("country")
        private String country;
        @JsonProperty("sunrise")
        private Long sunrise;
        @JsonProperty("sunset")
        private Long sunset;
    }
}
