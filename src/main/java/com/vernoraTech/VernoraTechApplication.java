package com.vernoraTech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class VernoraTechApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(VernoraTechApplication.class, args);
    }
}