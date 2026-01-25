package com.airline.pnr.application;

import com.airline.pnr.application.contract.BaggageDomainRepo;
import com.airline.pnr.application.contract.BookingDomainRepo;
import com.airline.pnr.application.contract.TicketDomainRepo;
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
        log.info("Starting Reactive Engine Logic for Booking retrieval: {}", pnr);
        long startTime = System.currentTimeMillis();
        
        // 1 Fetch Core Booking
        return bookingRepo.findByPnr(pnr).compose(booking -> {
            List<Integer> ids = booking.passengers().stream()
                                       .map(Passenger::passengerNumber).toList();
            
            log.info("Found {} passengers for PNR: {}", ids.size(), pnr);
            
            // 2. Parallel Fetch
            
            return fetchBaggagesAndTicketsInParallel(pnr, ids)
                    .map(aggregated -> {
                        List<BaggageAllowance> baggages = aggregated.resultAt(0);
                        Map<Integer, String> tickets = aggregated.resultAt(1);
                        
                        
                        // Filling the Booking
                        Booking result = booking.withDetails(baggages, tickets);
                        
                        
                        log.info("ompleted in {}ms for PNR: {}", System.currentTimeMillis() - startTime, pnr);
                        
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
//
//public Mono<BookingResponse> getBookingByPnr(String pnr) {
//    long start = System.currentTimeMillis();
//
//    return bookingRepo.findByBookingReference(pnr)
//                      .flatMap(booking -> {
//                          List<Integer> passengerIds = booking.passengers()
//                                                              .stream()
//                                                              .map(BookingEntity.PassengerDbo::passengerNumber)
//                                                              .toList();
//
//                          Mono<List<BaggageEntity>> baggagesMono = baggageRepo
//                                  .findByBookingReferenceAndPassengerNumberIn(pnr, passengerIds)
//                                  .collectList();
//
//                          Mono<List<TicketEntity>> ticketsMono = ticketRepo
//                                  .findByBookingReferenceAndPassengerNumberIn(pnr, passengerIds)
//                                  .collectList();
//
//                          return Mono.zip(baggagesMono, ticketsMono)
//                                     .map(tuple -> {
//                                         List<BaggageEntity> baggages = tuple.getT1();
//                                         List<TicketEntity> tickets = tuple.getT2();
//
//                                         // Publish event via Vert.x EventBus
//                                         vertx.eventBus().publish("pnr.fetched", pnr);
//
//                                         return BookingResponse.from(booking, baggages, tickets);
//                                     });
//                      })
//                      .doOnSuccess(res -> log.info("Booking {} aggregated in {}ms", pnr, System.currentTimeMillis() - start));
//}