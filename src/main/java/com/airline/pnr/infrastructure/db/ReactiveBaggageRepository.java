package com.airline.pnr.infrastructure.db;

import com.airline.pnr.infrastructure.entities.BaggageAllowanceEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface ReactiveBaggageRepository extends ReactiveMongoRepository<BaggageAllowanceEntity, String> {
    
    Flux<BaggageAllowanceEntity> findByBookingReferenceAndPassengerNumberIn(String pnr, List<Integer> ids);
}
