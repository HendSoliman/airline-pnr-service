package com.airline.pnr.infrastructure.access;

import com.airline.pnr.infrastructure.entities.BaggageAllowanceEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface ReactiveBaggageRepository extends ReactiveMongoRepository<BaggageAllowanceEntity, String> {
    
    Flux<BaggageAllowanceEntity> findByBookingReferenceAndPassengerNumberIn(String pnr, List<Integer> ids);
}