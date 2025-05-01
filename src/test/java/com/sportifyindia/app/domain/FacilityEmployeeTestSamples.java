package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FacilityEmployeeTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static FacilityEmployee getFacilityEmployeeSample1() {
        return new FacilityEmployee().id(1L).position("position1");
    }

    public static FacilityEmployee getFacilityEmployeeSample2() {
        return new FacilityEmployee().id(2L).position("position2");
    }

    public static FacilityEmployee getFacilityEmployeeRandomSampleGenerator() {
        return new FacilityEmployee().id(longCount.incrementAndGet()).position(UUID.randomUUID().toString());
    }
}
