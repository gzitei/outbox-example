package com.example.outboxexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class OutboxExampleApplication {

    public static void main(final String[] args) {
        SpringApplication.run(OutboxExampleApplication.class, args);
    }

}
