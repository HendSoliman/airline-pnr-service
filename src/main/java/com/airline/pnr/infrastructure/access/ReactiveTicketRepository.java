package com.airline.pnr.infrastructure.access;


import com.airline.pnr.infrastructure.entities.TicketEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface ReactiveTicketRepository extends ReactiveMongoRepository<TicketEntity, String> {
    Flux<TicketEntity> findByBookingReferenceAndPassengerNumberIn(String pnr, List<Integer> ids);
}