package com.airline.pnr.application;

import com.airline.pnr.application.contract.BaggageDomainRepo;
import com.airline.pnr.application.contract.BookingDomainRepo;
import com.airline.pnr.application.contract.TicketDomainRepo;
import com.airline.pnr.config.ThreadLog;
import com.airline.pnr.model.BaggageAllowance;
import com.airline.pnr.model.Booking;
import com.airline.pnr.model.Passenger;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Service
public class BookingAggregatorQueryService {
    
    private static final Logger log = LoggerFactory.getLogger(BookingAggregatorQueryService.class);
    
    private final BookingDomainRepo bookingRepo;
    private final BaggageDomainRepo baggageRepo;
    private final TicketDomainRepo ticketRepo;
    
    // Cleaned: Removed virtualTaskExecutor. Vert.x manages its own threads.
    public BookingAggregatorQueryService(BookingDomainRepo bookingRepo,
                                         BaggageDomainRepo baggageRepo,
                                         TicketDomainRepo ticketRepo) {
        this.bookingRepo = bookingRepo;
        this.baggageRepo = baggageRepo;
        this.ticketRepo = ticketRepo;
    }
    
    public Future<Booking> execute(String pnr) {
        log.info("Service START pnr={} | {}", pnr, ThreadLog.current());
//        log.info("Starting Reactive Engine Logic for Booking retrieval: {}", pnr);
        long startTime = System.currentTimeMillis();
        
        // 1 Fetch Core Booking  Vert.x takes over
        return bookingRepo.findByPnr(pnr)
                          .onSuccess(b ->
                                  log.info("Booking fetched | {}", ThreadLog.current())
                          ).compose(booking -> {
                    log.info("Compose passengers | {}", ThreadLog.current());
                    
                    List<Integer> ids = booking.passengers().stream()
                                       .map(Passenger::passengerNumber).toList();
                    
                    log.info("Found {} passengers for PNR: {}", ids.size(), pnr);
            
            // 2. Parallel Fetch
                    
                    return fetchBaggagesAndTicketsInParallel(pnr, ids).onSuccess(cf ->
                                                                              log.info("Parallel fetch DONE | {}", ThreadLog.current())
                                                                      )
                    .map(aggregated -> {
                        log.info("Aggregating results | {}", ThreadLog.current());
                        
                        List<BaggageAllowance> baggages = aggregated.resultAt(0);
                        Map<Integer, String> tickets = aggregated.resultAt(1);
                        
                        
                        // Filling the Booking
                        Booking result = booking.withDetails(baggages, tickets);
                        
                        
                        log.info("Completed in {}ms for PNR: {}", System.currentTimeMillis() - startTime, pnr);
                        
                        return result;
                    });
        });
    }
    
    private CompositeFuture fetchBaggagesAndTicketsInParallel(String pnr, List<Integer> ids) {
        return Future.all(
                baggageRepo.findBaggagesOfPassengers(ids, pnr),
                ticketRepo.findTicketUrls(ids, pnr)
        );
    }
}