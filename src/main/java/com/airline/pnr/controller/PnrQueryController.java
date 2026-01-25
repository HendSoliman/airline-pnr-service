package com.airline.pnr.controller;

import com.airline.pnr.application.BookingAggregatorQueryService;
import com.airline.pnr.config.ThreadLog;
import com.airline.pnr.model.Booking;
import com.airline.pnr.openapi.api.BookingApi;
import com.airline.pnr.openapi.model.BookingResponse;
import io.vertx.core.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RestController
public class PnrQueryController implements BookingApi {
    private static final Logger log = LoggerFactory.getLogger(PnrQueryController.class);
    
    private final BookingAggregatorQueryService bookingInfoQueryService;
    private final BookingResponseMapper mapper;

    public PnrQueryController(BookingAggregatorQueryService bookingInfoQueryService, BookingResponseMapper mapper) {
        this.bookingInfoQueryService = bookingInfoQueryService;
        this.mapper = mapper;
    }

    @Override
    public Mono<ResponseEntity<BookingResponse>> getBookingByPnr(String pnr, ServerWebExchange exchange) {
//        Future<Booking> bookingInfoResult =bookingInfoQueryService.execute(pnr);
//        // 2. Convert to Mono and map to the response
//        return Mono.fromCompletionStage(bookingInfoResult.toCompletionStage())
//                   .map(booking -> ResponseEntity.ok(mapper.toResponse(booking)))
//                   .defaultIfEmpty(ResponseEntity.notFound().build());
//        
//        
        log.info("Controller ENTER pnr={} | {}", pnr, ThreadLog.current());
        
        Future<Booking> future = bookingInfoQueryService.execute(pnr);
        
        log.info("Controller AFTER service call | {}", ThreadLog.current());
        
        return Mono.fromCompletionStage(future.toCompletionStage())
                   .doOnSubscribe(s ->
                           log.info("Mono SUBSCRIBED | {}", ThreadLog.current())
                   )
                   .doOnNext(b ->
                           log.info("Mono ON_NEXT | {}", ThreadLog.current())
                   )
                   .map(booking -> {
                       log.info("Mapping response | {}", ThreadLog.current());
                       return ResponseEntity.ok(mapper.toResponse(booking));
                   })
                   .doFinally(s ->
                           log.info("Controller FINALLY | {}", ThreadLog.current())
                   );
    }
        
    }
    


    