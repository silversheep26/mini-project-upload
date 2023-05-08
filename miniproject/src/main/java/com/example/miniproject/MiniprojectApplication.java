package com.example.miniproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MiniprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(MiniprojectApplication.class, args);
    }

}
