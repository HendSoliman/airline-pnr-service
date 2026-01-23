package com.airline.pnr.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class AbstractBaseApiTest {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }
    
    @Test
    @DisplayName("GIVEN a valid PNR, SHOULD return 200 and Booking Details")
    void shouldReturnBookingDetails() {
        given()
                .pathParam("pnr", "ABC123")
                .when()
                .get("/booking/{pnr}")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("bookingReference", equalTo("ABC123"))
                .body("passengers", hasSize(greaterThan(0)))
                .body("passengers[0].firstName", notNullValue());
    }
    
    @Test
    @DisplayName("GIVEN a non-existent PNR, SHOULD return 404 ProblemDetails")
    void shouldReturn404ForMissingPnr() {
        given()
                .pathParam("pnr", "NONEXIST")
                .when()
                .get("/booking/{pnr}")
                .then()
                .statusCode(404)
                .contentType("application/problem+json")
                .body("title", equalTo("Booking Not Found"))
                .body("status", is(404))
                .body("detail", containsString("NONEXIST"));
    }
}