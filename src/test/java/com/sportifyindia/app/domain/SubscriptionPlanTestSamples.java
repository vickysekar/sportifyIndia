package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SubscriptionPlanTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static SubscriptionPlan getSubscriptionPlanSample1() {
        return new SubscriptionPlan()
            .id(1L)
            .name("name1")
            .description("description1")
            .validityType("validityType1")
            .validityPeriod(1)
            .noOfPauseDays(1)
            .sessionLimit(1);
    }

    public static SubscriptionPlan getSubscriptionPlanSample2() {
        return new SubscriptionPlan()
            .id(2L)
            .name("name2")
            .description("description2")
            .validityType("validityType2")
            .validityPeriod(2)
            .noOfPauseDays(2)
            .sessionLimit(2);
    }

    public static SubscriptionPlan getSubscriptionPlanRandomSampleGenerator() {
        return new SubscriptionPlan()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .validityType(UUID.randomUUID().toString())
            .validityPeriod(intCount.incrementAndGet())
            .noOfPauseDays(intCount.incrementAndGet())
            .sessionLimit(intCount.incrementAndGet());
    }
}
