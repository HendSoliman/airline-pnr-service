package com.airline.pnr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;


@SpringBootApplication
@EnableReactiveMongoRepositories
@ComponentScan(basePackages = {"com.airline.pnr"}) // Force scan of all sub-packages
public class AirlinePnrServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirlinePnrServiceApplication.class, args);
	}

}
