package com.airline.pnr.config;

public final class ThreadLog {
    
    private ThreadLog() {
    }
    
    
    public static String current() {
        Thread t = Thread.currentThread();
        return String.format(
                "state=%s daemon=%s",
                t.getState(),
                t.isDaemon()
        );
    }
}
