### smoking gun
🔵 Waiting = 211 threads
🟢 Runnable = 11
🟡 Timed-Waiting = 8–38
🔴 Blocked = 0 


        ┌───────────────────────────────┐
        │        Live Threads: 233      │
        └───────────────────────────────┘
                   │
    ┌──────────────┴───────────────┐
    │                              │
🟢 Runnable: 11                 🔵 Waiting: 211
- Active computation             - Virtual threads suspended on I/O
- Stitching / Response building  - No heavy OS thread

🟡 Timed-Waiting: 8
- Minor locks / timeouts

Daemon: 229
- Scheduler, GC, background

✅ Observations From Diagram

Platform Threads: Huge blue wall (~55K) → memory hog, CPU underutilized.

Virtual Threads: Tiny blue portion (~211) → I/O suspension lightweight, CPU fully used.

Green threads: represent actual computation; virtual threads allow more CPU utilization without increasing thread count.

Scalability: Virtual threads scale massively better.