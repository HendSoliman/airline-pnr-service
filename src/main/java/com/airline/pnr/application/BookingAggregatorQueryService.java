package com.airline.pnr.application;

import com.airline.pnr.application.contract.BaggageDomainRepo;
import com.airline.pnr.application.contract.BookingDomainRepo;
import com.airline.pnr.application.contract.TicketDomainRepo;
import com.airline.pnr.domain.ThreadLog;
import com.airline.pnr.model.BaggageAllowance;
import com.airline.pnr.model.Booking;
import com.airline.pnr.model.Passenger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

@Service
public class BookingAggregatorQueryService {
    
    private static final Logger log = LoggerFactory.getLogger(BookingAggregatorQueryService.class);
    
    private final BookingDomainRepo bookingRepo;
    private final BaggageDomainRepo baggageRepo;
    private final TicketDomainRepo ticketRepo;
    
    public BookingAggregatorQueryService(BookingDomainRepo bookingRepo,
                                         BaggageDomainRepo baggageRepo,
                                         TicketDomainRepo ticketRepo) {
        this.bookingRepo = bookingRepo;
        this.baggageRepo = baggageRepo;
        this.ticketRepo = ticketRepo;
    }
    
    public Booking execute(String pnr) {

//       return executeWithoutVirtualThreads(pnr);
        return executeWithVirtualThreads(pnr);
    }
    
    private Booking executeWithoutVirtualThreads(String pnr) {
        ThreadLog.log(log, "ServiceStart", "Starting Business Logic for PNR: %s", pnr);
        long startTime = System.currentTimeMillis();
        
        // Blocking call — but now on a virtual thread
        Booking booking = bookingRepo.findByPnr(pnr);
        
        List<Integer> ids = booking.passengers().stream()
                                   .map(Passenger::passengerNumber)
                                   .toList();
        ThreadLog.log(log, "PassengersFound", "Found %d passengers for PNR: %s", ids.size(), pnr);
        
        // Still blocking, sequential
        List<BaggageAllowance> baggages =
                baggageRepo.findBaggagesOfPassengers(ids, pnr);
        ThreadLog.log(log, "BaggageFetched", "Fetched baggage for IDs: %s", ids);
        
        Map<Integer, String> tickets =
                ticketRepo.findTicketUrls(ids, pnr);
        ThreadLog.log(log, "TicketsFetched", "Fetched tickets for IDs: %s", ids);
        
        Booking result = booking.withDetails(baggages, tickets);
        
        long duration = System.currentTimeMillis() - startTime;
        ThreadLog.log(log, "ServiceEnd", "Business Logic Completed in %dms for PNR: %s", duration, pnr);
        
        return result;
        
    }
    
    private Booking executeWithVirtualThreads(String pnr) {
        long startTime = System.currentTimeMillis();
        ThreadLog.log(log, "ServiceStart", "Starting Virtual Thread Aggregation for PNR: %s", pnr);
        
        // 1. Get base booking
        Booking booking = bookingRepo.findByPnr(pnr);
        List<Integer> ids = booking.passengers().stream().map(Passenger::passengerNumber).toList();
        ThreadLog.log(log, "DataPrepped", "Base booking found. Preparing to fetch Baggage and Tickets in parallel for %d passengers", ids.size());
        
        // 2. Parallelize sub-calls using Virtual Threads
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            ThreadLog.log(log, "ParallelStart", "Forking Virtual Threads for Baggage and Ticket repositories...");
            
            var baggageFuture = executor.submit(() -> {
                ThreadLog.log(log, "BaggageTask", "Baggage sub-task started on %s", Thread.currentThread());
                return baggageRepo.findBaggagesOfPassengers(ids, pnr);
            });
            
            var ticketsFuture = executor.submit(() -> {
                ThreadLog.log(log, "TicketTask", "Ticket sub-task started on %s", Thread.currentThread());
                return ticketRepo.findTicketUrls(ids, pnr);
            });
            
            // .get() blocks the PARENT virtual thread until both child threads finish
            List<BaggageAllowance> baggages = baggageFuture.get();
            Map<Integer, String> tickets = ticketsFuture.get();
            
            long duration = System.currentTimeMillis() - startTime;
            ThreadLog.log(log, "ServiceEnd", "Parallel Aggregation Completed in %dms for PNR: %s", duration, pnr);
            
            return booking.withDetails(baggages, tickets);
        } catch (Exception e) {
            ThreadLog.log(log, "ServiceError", "Virtual Thread task failed for PNR: %s. Error: %s", pnr, e.getMessage());
            throw new RuntimeException("Virtual Thread Task Failed", e);
        }
    }
}