package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UtilityBookingsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UtilityBookings getUtilityBookingsSample1() {
        return new UtilityBookings().id(1L).bookedQuantity(1);
    }

    public static UtilityBookings getUtilityBookingsSample2() {
        return new UtilityBookings().id(2L).bookedQuantity(2);
    }

    public static UtilityBookings getUtilityBookingsRandomSampleGenerator() {
        return new UtilityBookings().id(longCount.incrementAndGet()).bookedQuantity(intCount.incrementAndGet());
    }
}
