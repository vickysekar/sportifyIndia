package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UtilitySlotsTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static UtilitySlots getUtilitySlotsSample1() {
        return new UtilitySlots().id(1L).maxCapacity(1).currentBookings(1);
    }

    public static UtilitySlots getUtilitySlotsSample2() {
        return new UtilitySlots().id(2L).maxCapacity(2).currentBookings(2);
    }

    public static UtilitySlots getUtilitySlotsRandomSampleGenerator() {
        return new UtilitySlots()
            .id(longCount.incrementAndGet())
            .maxCapacity(intCount.incrementAndGet())
            .currentBookings(intCount.incrementAndGet());
    }
}
