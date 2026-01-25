package com.airline.pnr.infrastructure.db;


import com.airline.pnr.infrastructure.entities.BookingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface BookingMongoRepository extends MongoRepository<BookingEntity, String> {
    Optional<BookingEntity> findByBookingReference(String pnr);
}
