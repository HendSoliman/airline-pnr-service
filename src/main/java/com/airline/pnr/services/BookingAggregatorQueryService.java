package com.airline.pnr.services;

import com.airline.pnr.model.BaggageAllowance;
import com.airline.pnr.model.Booking;
import com.airline.pnr.model.Passenger;
import com.airline.pnr.services.contract.BaggageDomainRepo;
import com.airline.pnr.services.contract.BookingDomainRepo;
import com.airline.pnr.services.contract.TicketDomainRepo;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Service to aggregate booking details including baggage allowances and ticket URLs
 */
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
    public Future<Booking> execute(String pnr) {
        long start = System.currentTimeMillis();
        log.info("Service: Fetching booking for PNR: {}", pnr);
        return bookingRepo.findByPnr(pnr)
                          .onSuccess(b -> log.info("Core booking fetched for pnr={}", pnr))
                          .onFailure(e -> log.warn("Booking aggregation failed for pnr={}", pnr))
                          .compose(this::aggregateBagsAndTickets)
                          .onSuccess(b -> log.info("Service DONE in {}ms", System.currentTimeMillis() - start));
    }
    
    
    private Future<Booking> aggregateBagsAndTickets(Booking booking) {
        var passengerIds = booking.passengers().stream()
                                  .map(Passenger::passengerNumber)
                                  .toList();
        
        // Parallel calls
        return fetchBaggageAndTickets(booking.bookingReference().value(), passengerIds)
                .map(details -> booking.withDetails(details.baggage(), details.tickets()));
    }
    
    private Future<BookingDetails> fetchBaggageAndTickets(String pnr, List<Integer> ids) {
        Future<List<BaggageAllowance>> baggageFuture =
                baggageRepo.findBagsOfPassengers(ids, pnr);
        
        Future<Map<Integer, String>> ticketFuture =
                ticketRepo.findTicketUrls(ids, pnr);
        
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