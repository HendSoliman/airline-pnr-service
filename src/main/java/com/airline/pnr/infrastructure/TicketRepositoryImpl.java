package com.airline.pnr.infrastructure;


import com.airline.pnr.infrastructure.access.ReactiveTicketRepository;
import com.airline.pnr.services.contract.TicketDomainRepo;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Repository
public class TicketRepositoryImpl implements TicketDomainRepo {
    
    private static final Logger log = LoggerFactory.getLogger(TicketRepositoryImpl.class);
    
    private final ReactiveTicketRepository repo;
    
    public TicketRepositoryImpl(ReactiveTicketRepository repo) {
        this.repo = repo;
    }
    
    /**
     * Finds ticket URLs for the given passenger IDs and PNR.
     *
     * @param ids List of passenger IDs
     * @param pnr Booking reference (PNR)
     * @return Future containing a Map of Passenger ID to Ticket URL
     */
    @Override
    public Future<Map<Integer, String>> findTicketUrls(List<Integer> ids, String pnr) {
        
        log.debug("Fetching ticket URLs for PNR: {} and Passengers: {}", pnr, ids);
        
        
  /*      return Future.fromCompletionStage(
                repo.findByBookingReferenceAndPassengerNumberIn(pnr, ids)
                    .collectMap(
                            TicketEntity::passengerNumber,
                            ticket -> Optional.ofNullable(ticket.ticketUrl())
                                              .flatMap(urlOpt -> urlOpt)
                                              .orElse("")
                    )
                    .toFuture()
        );
    }
    */
        return Future.fromCompletionStage(
                repo.findByBookingReferenceAndPassengerNumberIn(pnr, ids)
                    .flatMap(ticket -> {
                        
                        if (ticket.ticketUrl() != null && ticket.ticketUrl().isPresent()) {
                            return Mono.just(Map.entry(ticket.passengerNumber(), ticket.ticketUrl().get()));
                        }
                        return Mono.empty();
                    })
                    .collectMap(Map.Entry::getKey, Map.Entry::getValue)
                    .toFuture()
        );
    }
}