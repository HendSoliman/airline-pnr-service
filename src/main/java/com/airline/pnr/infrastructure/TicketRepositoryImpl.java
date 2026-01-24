package com.airline.pnr.infrastructure;


import com.airline.pnr.application.contract.TicketDomainRepo;
import com.airline.pnr.infrastructure.db.TicketMongoRepository;
import com.airline.pnr.infrastructure.entities.TicketEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class TicketRepositoryImpl implements TicketDomainRepo {
    
    private static final Logger log = LoggerFactory.getLogger(TicketRepositoryImpl.class);
    
    private final TicketMongoRepository mongoRepo; // The Spring Data interface
    
    public TicketRepositoryImpl(TicketMongoRepository mongoRepo) {
        this.mongoRepo = mongoRepo;
    }
    
    
    @Override
    public Map<Integer, String> findTicketUrls(List<Integer> ids, String pnr) {
        
        log.debug("Repository accessing MongoDB for Ticket...");
        
        
        return mongoRepo.findAllByPassengerIdsAndBookingReference(ids, pnr)
                        .stream()
                        .filter(ticket -> ticket.ticketUrl().isPresent())
                        .collect(Collectors.toMap(
                                TicketEntity::passengerNumber,
                                ticket -> ticket.ticketUrl().get()
                        ));
    }
    
}