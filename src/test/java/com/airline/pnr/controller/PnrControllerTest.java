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

//https://rest-assured.io/
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PnrControllerTest {
    
    @LocalServerPort
    private int port;
    
    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }
    
    @Test
    @DisplayName("GIVEN a valid PNR, returned 200 with expected PNR info")
    void should_returns_200_with_expected_pnr_info() {
        given().pathParam("pnr", "ABC123").when().get("/booking/{pnr}").then()// validation response
               .statusCode(200).contentType(ContentType.JSON).body("bookingReference", equalTo("ABC123")).body("passengers", hasSize(greaterThan(0))).body("passengers[0].firstName", notNullValue());
    }
    
    @Test
    @DisplayName("GIVEN a non-existent PNR, returned 404 ProblemDetails")
    void should_returns_404_for_non_found_pnr() {
        given().pathParam("pnr", "NONEXIST").when().get("/booking/{pnr}").then() // validation response
               .statusCode(404).contentType("application/problem+json").body("title", equalTo("Booking Not Found")).body("status", is(404)).body("detail", containsString("NONEXIST"));
    }
}