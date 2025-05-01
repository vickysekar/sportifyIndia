package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class TimeSlotsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TimeSlots getTimeSlotsSample1() {
        return new TimeSlots().id(1L);
    }

    public static TimeSlots getTimeSlotsSample2() {
        return new TimeSlots().id(2L);
    }

    public static TimeSlots getTimeSlotsRandomSampleGenerator() {
        return new TimeSlots().id(longCount.incrementAndGet());
    }
}
