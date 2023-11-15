package com.example.foodking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FoodKingApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodKingApplication.class, args);
    }

}
