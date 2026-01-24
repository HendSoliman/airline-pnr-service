# Booking Service - Threading Model & Execution Flow

This document explains the threading model and execution flow of the `GetBookingInfoQueryService`, which fetches booking, baggage, and ticket data in parallel for a given PNR.

---

## 1️⃣ Request Lifecycle – Owner Thread

In Spring MVC, **one Tomcat thread owns the request**:

```
http-nio-8080-exec-1
├─ parse HTTP request
├─ call controller
├─ enter service.execute()
├─ bookingRepo.findByPnr()   ❗ blocking
└─ identify passenger IDs
```

- `http-nio-8080-exec-1` is responsible for the **entire HTTP request lifecycle**.
- Blocking calls at this stage pause this thread but do not involve other threads.

---

## 2️⃣ Parallel Delegation – Worker Threads

The request thread delegates independent tasks to a dedicated **I/O thread pool (`ioPool`)** for parallel execution:

```
http-nio-8080-exec-1
├─ submit baggage task → ioPool
├─ submit ticket task  → ioPool
```

Worker threads execute the blocking MongoDB calls in parallel:

```
pool-2-thread-1        pool-2-thread-2
│                       │
├─ baggage Mongo        ├─ ticket Mongo
│   (blocking)          │   (blocking)
└─ return result        └─ return result
```

- `pool-2-thread-1` handles baggage fetching.
- `pool-2-thread-2` handles ticket fetching.
- Both threads run independently, reducing overall latency.

---

## 3️⃣ Rejoining – Stitching & Response

Once both futures complete, the **request thread resumes**:

```
http-nio-8080-exec-1
│
├─ fetch booking (blocking)
├─ submit baggage & ticket tasks
├─ join() ← waits here until both pool-2 threads finish ❗❗❗
├─ stitch data & build response
└─ send HTTP 200
```

- `.join()` blocks the request thread until results are ready.
- The request thread then combines results and returns the HTTP response.

---

## 4️⃣ Logs – Observed Execution

```
INFO [http-nio-8080-exec-1] GetBookingInfoQueryService - Starting Business Logic for PNR: GHTW42
DEBUG [http-nio-8080-exec-1] BookingRepositoryImpl - Repository accessing MongoDB for Booking...
INFO [http-nio-8080-exec-1] GetBookingInfoQueryService - Found 2 passengers for PNR: GHTW42
INFO [pool-2-thread-1] GetBookingInfoQueryService - Fetching Baggage for IDs: [1, 2]
DEBUG [pool-2-thread-1] BaggageRepositoryImpl - Repository accessing MongoDB for baggage...
INFO [pool-2-thread-2] GetBookingInfoQueryService - Fetching Ticket URLs for IDs: [1, 2]
DEBUG [pool-2-thread-2] TicketRepositoryImpl - Repository accessing MongoDB for Ticket...
INFO [http-nio-8080-exec-1] GetBookingInfoQueryService - Business Logic Completed in 146ms for PNR: GHTW42

```

### ✅ Observations

1. The **request thread** (`http-nio-8080-exec-1`) executes booking fetch, submits async tasks, and stitches results.
2. **I/O pool threads** (`pool-2-thread-1` & `pool-2-thread-2`) execute **blocking MongoDB operations** in parallel.
3. The **request thread waits** at `.join()` until the futures complete, then continues to send the response.
4. Dedicated pool usage prevents **ForkJoinPool starvation** and ensures predictable concurrency.

---

## 5️⃣ Key Takeaways

- **Ownership:** One thread per request (`http-nio-8080-exec-1`).
- **Parallelization:** Blocking DB calls are offloaded to dedicated pool threads.
- **Efficiency:** Independent I/O operations reduce latency.
- **Blocking:** Request thread blocks only at `.join()` safely.
- **Scalability:** Controlled pool size avoids thread starvation and improves throughput.

---

### 6️⃣ Recommendations

- Ensure `ioPool` is **singleton-scoped** to avoid thread leaks:
  ```java
  @Bean
  ExecutorService ioPool() {
      return Executors.newFixedThreadPool(10);
  }
  ```
- Consider **virtual threads** or **reactive drivers** for high-concurrency scenarios.
- Use logging to monitor thread usage and response times for performance tuning.

