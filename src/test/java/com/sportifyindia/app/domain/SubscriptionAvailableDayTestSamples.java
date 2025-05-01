package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SubscriptionAvailableDayTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SubscriptionAvailableDay getSubscriptionAvailableDaySample1() {
        return new SubscriptionAvailableDay().id(1L).daysOfWeek("daysOfWeek1");
    }

    public static SubscriptionAvailableDay getSubscriptionAvailableDaySample2() {
        return new SubscriptionAvailableDay().id(2L).daysOfWeek("daysOfWeek2");
    }

    public static SubscriptionAvailableDay getSubscriptionAvailableDayRandomSampleGenerator() {
        return new SubscriptionAvailableDay().id(longCount.incrementAndGet()).daysOfWeek(UUID.randomUUID().toString());
    }
}
