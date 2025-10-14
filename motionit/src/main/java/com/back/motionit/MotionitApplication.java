package com.back.motionit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MotionitApplication {

    public static void main(String[] args) {
        SpringApplication.run(MotionitApplication.class, args);
    }

}
