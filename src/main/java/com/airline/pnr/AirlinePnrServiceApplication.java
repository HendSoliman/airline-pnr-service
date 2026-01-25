package com.airline.pnr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication(exclude = {
        org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration.class
})
@ComponentScan(basePackages = {"com.airline.pnr"}) // Force scan of all sub-packages
public class AirlinePnrServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AirlinePnrServiceApplication.class, args);
	}

}
