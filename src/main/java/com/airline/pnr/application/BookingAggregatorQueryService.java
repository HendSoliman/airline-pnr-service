package com.airline.pnr.application;

import com.airline.pnr.application.contract.BaggageDomainRepo;
import com.airline.pnr.application.contract.BookingDomainRepo;
import com.airline.pnr.application.contract.TicketDomainRepo;
import com.airline.pnr.config.ThreadLog;
import com.airline.pnr.domain.valueobjects.Pnr;
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
        long start = System.currentTimeMillis();
        
        return fetchCoreBooking(pnr)
                .compose(this::composeBookingBaggageAndTicket)
                .map(enrichedBooking -> logAndReturn(enrichedBooking, start));
    }
    
    
    private Booking logAndReturn(Booking booking, long start) {
        log.info(
                "Service DONE in {}ms | {}",
                System.currentTimeMillis() - start,
                ThreadLog.current()
        );
        return booking;
    }
    
    // --- 2️⃣ Fetch core booking ---
    private Future<Booking> fetchCoreBooking(String pnr) {
        return bookingRepo.findByPnr(pnr)
                          .onSuccess(b -> log.info("Booking fetched | {}", ThreadLog.current()));
    }
    
    // --- 3️⃣ Compose dependent work ---
    private Future<Booking> composeBookingBaggageAndTicket(Booking booking) {
        List<Integer> passengerIds = extractPassengerIds(booking);
        log.info("Found {} passengers | {}", passengerIds.size(), ThreadLog.current());
        
        return fetchBaggagesAndTickets(booking.bookingReference(), passengerIds)
                .onSuccess(v -> log.info("Parallel fetch DONE | {}", ThreadLog.current()))
                .map(cf -> aggregateBookingDetails(booking, cf));
    }
    
    // --- Helper: extract passenger IDs ---
    private List<Integer> extractPassengerIds(Booking booking) {
        return booking.passengers()
                      .stream()
                      .map(Passenger::passengerNumber)
                      .toList();
    }
    
    // --- 4️⃣ Parallel fan-out ---
    private Future<CompositeFuture> fetchBaggagesAndTickets(
            Pnr pnr,
            List<Integer> passengerIds
    ) {
        return Future.all(
                baggageRepo.findBaggagesOfPassengers(passengerIds, pnr.value()),
                ticketRepo.findTicketUrls(passengerIds, pnr.value())
        );
    }
    
    // --- 5️⃣ Aggregate result ---
    private Booking aggregateBookingDetails(Booking booking, CompositeFuture cf) {
        List<BaggageAllowance> baggages = cf.resultAt(0);
        Map<Integer, String> tickets = cf.resultAt(1);
        return booking.withDetails(baggages, tickets);
    }
}
