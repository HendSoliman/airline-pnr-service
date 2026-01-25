package com.airline.pnr.infrastructure.db;

import com.airline.pnr.infrastructure.entities.TicketEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface TicketMongoRepository extends ReactiveMongoRepository<TicketEntity, String> {
    Flux<TicketEntity> findByBookingReferenceAndPassengerNumberIn(String pnr, List<Integer> ids);
}