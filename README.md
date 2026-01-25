#  DVT Task  -PNR Information Aggregator 
## Overview

### What You'll Build
A **RESTful API** that retrieves comprehensive booking information for a given Passenger Name Record (PNR). The service aggregates:

1. **Trip Information**: Passenger details, flight information
2. **Baggage Allowances**: Checked and carry-on limits per passenger
3. **ETicket Information**: Tickets per passenger (handle missing tickets gracefully)

The service must be **reactive, event-driven, and resilient**, with **MongoDB as the source of truth**.

## Business Domain: Airline Booking System

### Domain Overview
building a **backend service** that aggregates booking information for a PNR. Each booking contains:

- **Passengers**: Names, seats, optional customer IDs
- **Flights**: Flight number, departure/arrival airports, timestamps
- **Baggage**: Checked and carry-on allowances
- **Tickets**: ETicket URLs (if available)

### What is Matters
The service must handle:
- Data aggregation from multiple sources
- Event-driven notifications when a PNR is retrieved

## Technology Stack & Requirements

###  Technology Stack
| Component            | Technology              | Version | Purpose                                       |
|----------------------|-------------------------|---------|-----------------------------------------------|
| **Language**         | Java (OpenJDK/Oracle)   | 21 LTS  | Modern language features & performance        |
| **Framework**        | Eclipse Vert.x          | 5.0.4+  | Reactive, non-blocking application platform   |
| **Database**         | MongoDB Database        | still   | Lightweight, fast in-memory SQL database      |
| **Testing**          | JUnit 5 + + Mockito     | Latest  | Async-aware testing with coverage enforcement |
| **Build System**     | Apache Maven            | 3.8+    | Dependency management & lifecycle             |
| **Coverage**         | JaCoCo                  | 0.8.12+ | Code coverage analysis & enforcement          |
| **Containerization** | Docker + Docker Compose | Latest  | Local dev and deployment readiness            |

###  Tech Reference
- [Eclipse Vert.x Documentation](https://vertx.io/docs/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [ Monitoring Java Applications with Prometheus and Grafana](https://www.youtube.com/watch?v=27Yc3gdeuQ0)
- [ Introduction to Spring Data MongoDB ](https://www.baeldung.com/spring-data-mongodb-tutorial)
- https://docs.spring.io/spring-data/mongodb/reference/mongodb/mapping/custom-conversions.html
- https://docs.spring.io/spring-data/mongodb/reference/mongodb/mapping/document-references.html#page-title
- [ Pattern Matching for switch Expressions and Statements ](https://docs.oracle.com/en/java/javase/22/language/pattern-matching-switch-expressions-and-statements.html#GUID-E69EEA63-E204-41B4-AA7F-D58B26A3B232)
- [Spring WebClient vs. RestTemplate](https://www.baeldung.com/spring-webclient-resttemplate)
- -https://docs.spring.io/spring-framework/reference/testing/webtestclient.html
- Tests: https://rest-assured.io/ Framework Independent: I
- https://medium.com/@eng.hibrahem/ddd-and-clean-architecture-part-1-9a514b964395
- https://mapstruct.org/
- https://www.baeldung.com/spring-vertx
- https://www.baeldung.com/vertx
- https://vertx.io/docs/vertx-mongo-client/java/
- Vert.x OpenAPI Preview https://vertx.io/docs/vertx-openapi/java/#_openapicontract

## REST API Endpoints

### Retrieve Booking by PNR
- **GET** `/booking/{pnr}`
- **Response Example**
```json
{
  "pnr": "GHTW42",
  "cabinClass": "ECONOMY",
  "passengers": [
    {
      "passengerNumber": 1,
      "fullName": "James Morgan McGill",
      "seat": "32D"
    },
    {
      "passengerNumber": 2,
      "customerId": "1216",
      "fullName": "Charles McGill",
      "seat": "31D",
      "ticketUrl": "emirates.com?ticket=someTicketRef"
    }
  ],
  "flights": [
    {
      "flightNumber": "EK231",
      "departureAirport": "DXB",
      "departureTimeStamp": "2025-11-11T02:25:00+00:00",
      "arrivalAirport": "IAD",
      "arrivalTimeStamp": "2025-11-11T08:10:00+00:00"
    }
  ]
}
```
### Optional Bonus Endpoints

- Hide PNR via customer ID path parameter
- WebSocket endpoint broadcasting pnr.fetched events

## Key Considerations

### Use Spring Boot for:
* REST controllers (@RestController)
* Configuration / Dependency injection
* Exposing endpoints
* Using either Spring Data MongoDB or reactive repository
###  Use Vert.x for:
* Async CRUD execution
* Event-driven messaging (e.g., when a PNR is fetched, publish an event)
* Non-blocking DB calls (optional if you use Vert.x Mongo client directly)
### Use MongoDB (preferred) or CouchDB for persistence.


## Project Structure

```
TODO 
src/
└── src/main/java/com/company/pnr
com.airline.pnr
├── api                         <-- REST Layer (Web)
│   ├── PnrController.java      <-- Implements generated BookingApi
│   └── PnrControllerConverter.java <-- Maps Read Model -> OpenAPI DTOs
│
├── application                 <-- Orchestration Layer (Pure Logic)
│   └── GetBookingInfoQueryService.java <-- Orchestrates parallel data fetching
│
├── model                       <-- Read Models (Data Contracts)
│   ├── BookingInformation.java <-- Record with .withDetails() stitching logic
│   ├── Passenger.java          <-- Domain Record
│   ├── Flight.java             <-- Domain Record
│   └── BaggageAllowance.java   <-- Domain Record
│
├── infrastructure              <-- Implementation Detail (The "How")
│   ├── db                      <-- Low-level Database Access
│   │   ├── entities            <-- @Document DBOs (BookingDbo, BaggageDbo, etc.)
│   │   ├── BookingRepository.java <-- Spring Data Mongo Interface
│   │   ├── BaggageRepository.java <-- Spring Data Mongo Interface
│   │   └── TicketRepository.java  <-- Spring Data Mongo Interface
│   │
│   └── persistence             <-- Domain-to-DB Bridge
│       ├── PnrQueryRepository.java    <-- Interface (defined in application or here)
│       ├── PnrQueryRepositoryImpl.java <-- Hides DBOs, returns Read Models
│       └── PnrDbMapper.java           <-- Maps DBOs -> Read Models
│
└── domain
    ├── exception
    │   └── BookingNotFoundException.java
    └── valueobjects
        ├── Pnr.java
        └── CustomerId.java
│   └── resources/
│       ├── application.properties            # Application configuration
├── test/
│   └── java/com
│       └── todo.java     # Sample test structure
├── Dockerfile                          # Container definition
├── .dockerignore                       # Docker ignore patterns
└── pom.xml                            # Maven configuration
```


### Technical Excellence Standards

#### Reactive Programming Mastery
- **Event Loop Affinity**: All operations must be event-loop safe
- **Future Composition**: Proper chaining of async operations
- **Error Propagation**: Graceful handling in reactive streams
- **Backpressure Management**: Handle overwhelming request loads

#### Performance & Scalability
- **Non-blocking I/O**: Zero blocking operations in request handling
- **Resource Efficiency**: Minimal thread usage, maximum throughput
- **Memory Management**: Proper resource cleanup and leak prevention

#### Code Quality Standards
- **Separation of Concerns**: Clean architecture with distinct layers
- **SOLID Principles**: Well-structured, maintainable codebase
- **Error Handling**: Comprehensive exception management
- **Test Coverage**: Minimum 85% with meaningful assertions

## Database Schema
- TODO 

## Getting Started
-- TODO
## Swagger Access
``` http://localhost:8080/swagger-ui/index.html```



### Setup Instructions

1. **Clone the project**
   ```bash
   Clone https://github.com/HendSoliman/airline-pnr-service.git
   ```

2. **Install dependencies**
   ```bash
   mvn clean install
   ```

3. **Run the application**
   ```bash
   mvn exec:java
   # OR
   mvn compile exec:java -Dexec.mainClass="com.dvtsoftware.airline.booking.MainVerticle"
   ```

4. **Run tests**
   ```bash
   mvn test
   ```

5. **Check test coverage**
   ```bash
   # Run tests with coverage report
   mvn clean verify
   
   # View coverage report
   open target/site/jacoco/index.html
   ```
6. **Monitoring**
- Connect Grafana to Prometheus
   ```bash
Open Grafana (http://localhost:3000)
open Prometheus ( http://localhost:9090/targets )
Run Jmeter : jmeter
   ```
