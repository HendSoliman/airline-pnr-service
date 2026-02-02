package com.airline.pnr.services;

import com.airline.pnr.domain.events.PnrFetchedEvent;
import com.airline.pnr.model.BaggageAllowance;
import com.airline.pnr.model.Booking;
import com.airline.pnr.model.Passenger;
import com.airline.pnr.services.contract.BaggageDomainRepo;
import com.airline.pnr.services.contract.BookingDomainRepo;
import com.airline.pnr.services.contract.EventPublisherPort;
import com.airline.pnr.services.contract.TicketDomainRepo;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Service to aggregate booking details including baggage allowances and ticket URLs
 */
@Service
public class BookingAggregatorQueryService {
    
    private static final Logger log = LoggerFactory.getLogger(BookingAggregatorQueryService.class);
    
    private final BookingDomainRepo bookingRepo;
    private final BaggageDomainRepo baggageRepo;
    private final TicketDomainRepo ticketRepo;
    
    private final EventPublisherPort eventPublisher;
    
    public BookingAggregatorQueryService(BookingDomainRepo bookingRepo,
                                         BaggageDomainRepo baggageRepo,
                                         TicketDomainRepo ticketRepo, EventPublisherPort eventPublisher) {
        this.bookingRepo = bookingRepo;
        this.baggageRepo = baggageRepo;
        this.ticketRepo = ticketRepo;
        this.eventPublisher = eventPublisher;
    }
    public Future<Booking> execute(String pnr) {
        long start = System.currentTimeMillis();
        log.info("Service: Fetching booking for PNR: {}", pnr);
        log.info("Service builds an async pipeline.: {}", Thread.currentThread());
        
        
        return bookingRepo.findByPnr(pnr)
                          .onSuccess(b -> log.info("Core booking fetched for pnr={}", pnr))
                          .onFailure(e -> log.warn("Core booking fetch failed for pnr={}", pnr))
                          .compose(this::aggregateBagsAndTickets)
                          .onSuccess(b -> {
                             eventPublisher.publish(new PnrFetchedEvent(pnr));
                              log.info("Service DONE in {}ms", System.currentTimeMillis() - start);
                          }).onFailure(e -> log.warn("Service FAILED in {}ms for pnr={}", System.currentTimeMillis() - start, pnr, e));
    }
    
    
    private Future<Booking> aggregateBagsAndTickets(Booking booking) {
        var passengerIds = booking.passengers().stream()
                                  .map(Passenger::passengerNumber)
                                  .toList();
        
        // Parallel calls
        return fetchBaggageAndTickets(booking.bookingReference().value(), passengerIds)
                .recover(ex -> {
                    log.warn("Baggage enrichment failed, fallback empty. pnr={}", booking.bookingReference().value(), ex);
                    return Future.succeededFuture(new BookingDetails(List.of(), Map.of()));
                })
                .map(details -> booking.withDetails(details.baggage(), details.tickets()));
    }
    
    private Future<BookingDetails> fetchBaggageAndTickets(String pnr, List<Integer> ids) {
        
        Future<List<BaggageAllowance>> baggageFuture = baggageRepo.findBagsOfPassengers(ids, pnr)
                                                                  .timeout(20, TimeUnit.MILLISECONDS)
                                                                  .onSuccess(t -> log.info("Baags repo success pnr={}, count={}", pnr, t.size()))
                                                                  .onFailure(fail -> log.warn("Baggage fetch failed for pnr={}", pnr, fail))
                                                                  .recover(ex -> {
                                                                      log.warn("Revovering from bggage...... failed, fallback empty. pnr={}", pnr, ex);
                                                                      return Future.succeededFuture(List.of());
                                                                  });


//        ----------
        Future<Map<Integer, String>> ticketFuture = ticketRepo.findTicketUrls(ids, pnr)
                                                              .timeout(20, TimeUnit.MILLISECONDS)
                                                              .onSuccess(t -> log.info("Ticket repo success pnr={}, count={}", pnr, t.size()))
                                                              .onFailure( fail -> log.warn("Ticket fetch failed for pnr={}", pnr, fail))
                                                              .recover(ex -> {
                                                                        log.warn("Recovering,,,,,, Ticket failed, fallback empty. pnr={}", pnr, ex);
                                                                        return Future.succeededFuture(Map.of());
        });
        
        return Future.all(baggageFuture, ticketFuture)
                     .map(compositeFuture -> new BookingDetails(
                             compositeFuture.resultAt(0),
                             compositeFuture.resultAt(1)
                     ));
    }
    
    /**
     * Carrier
     */
    private record BookingDetails(
            List<BaggageAllowance> baggage,
            Map<Integer, String> tickets
    )
    {
    }
}