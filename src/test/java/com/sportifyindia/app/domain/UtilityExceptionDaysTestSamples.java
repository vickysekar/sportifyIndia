package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class UtilityExceptionDaysTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static UtilityExceptionDays getUtilityExceptionDaysSample1() {
        return new UtilityExceptionDays().id(1L).reason("reason1").nestedTimeslots("nestedTimeslots1");
    }

    public static UtilityExceptionDays getUtilityExceptionDaysSample2() {
        return new UtilityExceptionDays().id(2L).reason("reason2").nestedTimeslots("nestedTimeslots2");
    }

    public static UtilityExceptionDays getUtilityExceptionDaysRandomSampleGenerator() {
        return new UtilityExceptionDays()
            .id(longCount.incrementAndGet())
            .reason(UUID.randomUUID().toString())
            .nestedTimeslots(UUID.randomUUID().toString());
    }
}
