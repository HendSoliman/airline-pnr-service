package com.airline.pnr.infrastructure.db;


import com.airline.pnr.infrastructure.entities.BookingEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ReactiveBookingRepository extends ReactiveMongoRepository<BookingEntity, String> {
    
    Mono<BookingEntity> findByBookingReference(String pnr);
}