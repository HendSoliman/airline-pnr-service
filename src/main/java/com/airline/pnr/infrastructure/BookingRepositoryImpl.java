package com.airline.pnr.infrastructure;


import com.airline.pnr.application.contract.BookingDomainRepo;
import com.airline.pnr.domain.exception.BookingNotFoundException;
import com.airline.pnr.infrastructure.db.ReactiveBookingRepository;
import com.airline.pnr.infrastructure.mapper.BookingEntityMapper;
import com.airline.pnr.model.Booking;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class BookingRepositoryImpl implements BookingDomainRepo {
    
    private static final Logger log = LoggerFactory.getLogger(BookingRepositoryImpl.class);
    
    private final ReactiveBookingRepository repo;
    private final BookingEntityMapper mapper;
    
    public BookingRepositoryImpl(ReactiveBookingRepository repo, BookingEntityMapper mapper) {
        this.repo = repo;
        this.mapper = mapper;
    }
    @Override
    public Future<Booking> findByPnr(String pnr) {
        return Future.fromCompletionStage(
                repo.findByBookingReference(pnr)
                    .map(mapper::toReadModel)
                    .switchIfEmpty(Mono.error(new BookingNotFoundException(pnr)))
                    .toFuture()
        );
        
    }
}
//    @Override
//    public Future<Booking> findByPnr(String pnr) {
//
//        log.debug("Repo ENTER pnr={} | {}", pnr, ThreadLog.current());
//
//        return Future.fromCompletionStage(
//                repo.findByBookingReference(pnr)
//                    .map(mapper::toReadModel)
//                    .switchIfEmpty(Mono.error(new BookingNotFoundException(pnr)))
//                    .toFuture()
//        );
//    }
