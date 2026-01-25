package com.airline.pnr.domain;

import org.slf4j.Logger;

public final class ThreadLog {
    
    private ThreadLog() {

    }
    
    public static void log(Logger log, String prefix, String message, Object... args) {
        Thread t = Thread.currentThread();
        String threadInfo = String.format("[%s]",
                t.getState());
        
        log.info("{} {} {}", prefix, threadInfo, String.format(message, args));
    }
}
