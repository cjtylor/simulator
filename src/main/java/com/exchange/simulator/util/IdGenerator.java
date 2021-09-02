package com.exchange.simulator.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A dummy sequence generator
 *
 */
public class IdGenerator {
    private static AtomicLong seed = new AtomicLong(0);
    public static String genOrderID() {
        return "ord-" + next();
    }
    public static long next() {
        return seed.incrementAndGet();
    }
}
