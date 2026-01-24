package com.airline.pnr.infrastructure.db;

import com.airline.pnr.infrastructure.entities.BaggageAllowanceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface BaggageMongoRepository extends MongoRepository<BaggageAllowanceEntity, String> {

    @Query("{ 'passengerNumber': { $in: ?0 }, 'bookingReference': ?1 }")
    List<BaggageAllowanceEntity> findAllByPassengerIdsAndBookingReference(List<Integer> passengerIds, String bookingReference);
}