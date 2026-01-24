### crime scene 
🔵 Waiting = 55K threads
🟢 Runnable = 8-207
🟡 Timed-Waiting = 36
🔴 Blocked = 0
         ┌───────────────────────────┐
         │       Platform Threads    │
         │      Total: 55,000        │
         └───────────────────────────┘
                    │
        ┌───────────┴───────────┐
        │                       │
┌─────────────┐        ┌───────────────┐
│  Waiting    │        │  Timed-Waiting│
│  54,900     │        │     36        │
└─────────────┘        └───────────────┘
│                       │
│                       │
▼                       ▼
Threads blocked on I/O       Threads waiting on locks
- bookingRepo.findByPnr()    - join() on CompletableFuture
- join() on futures          - minor kernel waits
- MongoDB/network calls

      ┌─────────────┐
      │  Runnable   │
      │    8-207    │
      └─────────────┘
       │
       ▼
Active threads actually running business logic
(CPU doing work ~33%)

        ┌─────────────┐
        │  New / Term │
        │    0        │
        └─────────────┘


⚠️ Observations

The Waiting Wall

54,900 threads are just waiting — no CPU usage, huge RAM overhead.

Each thread is a heavy OS object consuming memory even if idle.

CPU vs Thread Count Paradox

CPU max ~33%, threads ~55K → most threads are blocked on I/O, not running.

Context switching dominates, not actual computation.

System Load

Load ~3.9 – 7.4 on 11 CPUs → kernel overwhelmed by thread management, not app work.

Scalability ceiling reached: more threads = more memory + slower response.

Impact

Adding more Platform Threads is dangerous → OutOfMemoryError.

Performance is I/O-bound, not CPU-bound.

        ┌─────────────┐
        │  Daemon     │
        │   43-213    │
        └─────────────┘



        ┌───────────────────────────────┐
        │      Platform Threads          │
        │      Total: 55,000            │
        └───────────────────────────────┘
                   │
    ┌──────────────┴───────────────┐
    │                              │
🔵 Waiting: 54,900          🟡 Timed-Waiting: 36
(Blocked on I/O or join())  (Locks / short waits)
│                              │
▼                              ▼
- Threads consume memory        - Minimal CPU usage
- Idle / blocking              - Minimal CPU usage

🟢 Runnable: 8-207
- Threads actually running
- CPU ~33%

🔴 Blocked: 0
- None active

Daemon: 43-213
- Background tasks / GC threads
