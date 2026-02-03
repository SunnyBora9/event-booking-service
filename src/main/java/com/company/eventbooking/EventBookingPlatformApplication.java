package com.company.eventbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;


@SpringBootApplication
@EnableJpaRepositories("com.company.eventbooking.repository")
@EntityScan("com.company.eventbooking.entity")
@ComponentScan(basePackages ="com.company.eventbooking")
@EnableKafka
public class EventBookingPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventBookingPlatformApplication.class, args);
	}

}
