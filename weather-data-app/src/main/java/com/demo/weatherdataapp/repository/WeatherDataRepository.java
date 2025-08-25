package com.demo.weatherdataapp.repository;

import com.demo.weatherdataapp.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {
    @Query("SELECT w FROM WeatherData w WHERE LOWER(w.city) = LOWER(:city) ORDER BY w.lastUpdated DESC")
    List<WeatherData> findByCityIgnoreCaseOrderByLastUpdatedDesc(@Param("city") String city);

    @Query("SELECT w FROM WeatherData w WHERE w.zipCode = :zipCode ORDER BY w.lastUpdated DESC")
    List<WeatherData> findByZipCodeOrderByLastUpdatedDesc(@Param("zipCode") String zipCode);

    @Query("SELECT w FROM WeatherData w WHERE LOWER(w.city) = LOWER(:city) ORDER BY w.lastUpdated DESC LIMIT 1")
    Optional<WeatherData> findLatestByCityIgnoreCase(@Param("city") String city);

    @Query("SELECT w FROM WeatherData w WHERE w.zipCode = :zipCode ORDER BY w.lastUpdated DESC LIMIT 1")
    Optional<WeatherData> findLatestByZipCode(@Param("zipCode") String zipCode);

    @Query("SELECT w FROM WeatherData w WHERE w.lastUpdated < :threshold")
    List<WeatherData> findDataOlderThan(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT DISTINCT w.city FROM WeatherData w")
    List<String> findDistinctCities();

    @Query("SELECT DISTINCT w.zipCode FROM WeatherData w WHERE w.zipCode IS NOT NULL")
    List<String> findDistinctZipCodes();

    @Query("DELETE FROM WeatherData w WHERE w.city = :city AND w.id NOT IN " +
            "(SELECT w2.id FROM WeatherData w2 WHERE w2.city = :city ORDER BY w2.lastUpdated DESC LIMIT :keep)")
    void deleteOldRecordsForCity(@Param("city") String city, @Param("keep") int keep);
}
