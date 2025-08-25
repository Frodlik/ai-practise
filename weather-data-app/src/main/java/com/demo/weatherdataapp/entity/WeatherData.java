package com.demo.weatherdataapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String city;

    private String country;

    private String zipCode;

    @Column(nullable = false)
    private Double temperature;

    private Double feelsLike;

    private Integer humidity;

    private Integer pressure;

    private String description;

    private String mainWeather;

    private Double windSpeed;

    private Integer windDirection;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false)
    private LocalDateTime lastUpdated;

    @PrePersist
    private void prePersist() {
        if (timestamp == null && lastUpdated == null) {
            timestamp = LocalDateTime.now();
            lastUpdated = timestamp;
        }
    }

    @PreUpdate
    private void preUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}
