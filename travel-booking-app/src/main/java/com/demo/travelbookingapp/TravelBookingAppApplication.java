package com.demo.travelbookingapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TravelBookingAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(TravelBookingAppApplication.class, args);
    }
}
