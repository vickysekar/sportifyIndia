package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UtilityAvailableDaysTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UtilityAvailableDays getUtilityAvailableDaysSample1() {
        return new UtilityAvailableDays().id(1L).daysOfWeek("daysOfWeek1");
    }

    public static UtilityAvailableDays getUtilityAvailableDaysSample2() {
        return new UtilityAvailableDays().id(2L).daysOfWeek("daysOfWeek2");
    }

    public static UtilityAvailableDays getUtilityAvailableDaysRandomSampleGenerator() {
        return new UtilityAvailableDays().id(longCount.incrementAndGet()).daysOfWeek(UUID.randomUUID().toString());
    }
}
