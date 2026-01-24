package com.airline.pnr.infrastructure.db;

import com.airline.pnr.infrastructure.entities.TicketEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface TicketMongoRepository extends MongoRepository<TicketEntity, String> {
    
    @Query("{ 'passengerNumber': { $in: ?0 }, 'bookingReference': ?1 }")
    List<TicketEntity> findAllByPassengerIdsAndBookingReference(List<Integer> passengerIds, String pnr);
    
}
