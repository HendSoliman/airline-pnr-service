package com.airline.pnr.infrastructure;

import com.airline.pnr.domain.exception.BookingNotFoundException;
import com.airline.pnr.infrastructure.access.ReactiveBookingRepository;
import com.airline.pnr.infrastructure.mappers.BookingMapper;
import com.airline.pnr.model.Booking;
import com.airline.pnr.services.contract.BookingDomainRepo;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BookingRepositoryImpl implements BookingDomainRepo {
    private static final Logger log = LoggerFactory.getLogger(BookingRepositoryImpl.class);
    
    private final ReactiveBookingRepository repo;
    private final BookingMapper mapper;
    
    public BookingRepositoryImpl(ReactiveBookingRepository repo, BookingMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }
    @Override
    public Future<Booking> findByPnr(String pnr) {
        return Future.fromCompletionStage(
                repo.findByBookingReference(pnr)
                    .switchIfEmpty(Mono.error(new BookingNotFoundException(pnr)))
                    .map(mapper::toReadModel)
                    .toFuture()
        );
        
    }
}