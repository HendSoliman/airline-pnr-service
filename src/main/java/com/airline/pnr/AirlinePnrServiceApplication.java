package com.airline.pnr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
@ComponentScan(basePackages = {"com.airline.pnr"})
public class AirlinePnrServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirlinePnrServiceApplication.class, args);
	}

}
