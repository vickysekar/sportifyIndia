package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class UtilityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Utility getUtilitySample1() {
        return new Utility()
            .id(1L)
            .name("name1")
            .description("description1")
            .termsAndConditions("termsAndConditions1")
            .maxCapacity(1)
            .requirements("requirements1");
    }

    public static Utility getUtilitySample2() {
        return new Utility()
            .id(2L)
            .name("name2")
            .description("description2")
            .termsAndConditions("termsAndConditions2")
            .maxCapacity(2)
            .requirements("requirements2");
    }

    public static Utility getUtilityRandomSampleGenerator() {
        return new Utility()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .termsAndConditions(UUID.randomUUID().toString())
            .maxCapacity(intCount.incrementAndGet())
            .requirements(UUID.randomUUID().toString());
    }
}
