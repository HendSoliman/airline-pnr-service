package com.airline.pnr.application;

import com.airline.pnr.application.contract.BaggageDomainRepo;
import com.airline.pnr.application.contract.BookingDomainRepo;
import com.airline.pnr.application.contract.TicketDomainRepo;
import com.airline.pnr.model.Booking;
import com.airline.pnr.model.Passenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
public class BookingAggregatorQueryService {
    
    private static final Logger log = LoggerFactory.getLogger(BookingAggregatorQueryService.class);
    
    private final Executor virtualTaskExecutor;
    
    private final BookingDomainRepo bookingRepo;
    private final BaggageDomainRepo baggageRepo;
    private final TicketDomainRepo ticketRepo;
    
    public BookingAggregatorQueryService(@Qualifier("virtualTaskExecutor") Executor virtualTaskExecutor, BookingDomainRepo bookingRepo, BaggageDomainRepo baggageRepo, TicketDomainRepo ticketRepo) {
        this.virtualTaskExecutor = virtualTaskExecutor;
        this.bookingRepo = bookingRepo;
        this.baggageRepo = baggageRepo;
        this.ticketRepo = ticketRepo;
    }
    
    
    public Booking execute(String pnr) {

        log.info("Starting Business Logic for PNR: {}", pnr);
        long startTime = System.currentTimeMillis();
        // 1 Fetch Core Booking
        Booking booking = bookingRepo.findByPnr(pnr);
        List<Integer> ids = booking.passengers().stream().map(Passenger::passengerNumber).toList();
        
        log.info("Found {} passengers for PNR: {}",ids.size(), pnr);
        
        // 2 Parallel Fetch with Thread Logging
        var baggageFuture = CompletableFuture.supplyAsync(() -> {
            log.info("Fetching Baggage for IDs: {}",  ids);
            return baggageRepo.findBaggagesOfPassengers(ids, pnr);
        },virtualTaskExecutor);
        
        
        var ticketFuture = CompletableFuture.supplyAsync(() -> {
            log.info("Fetching Ticket URLs for IDs: {}", ids);
            return ticketRepo.findTicketUrls(ids, pnr);
        },virtualTaskExecutor);
        // 3 Stitching
        Booking result = booking.withDetails(baggageFuture.join(), ticketFuture.join());
        
        long duration = System.currentTimeMillis() - startTime;
        log.info("Business Logic Completed in {}ms for PNR: {}", duration, pnr);
        
        
        return result;
    }
}
