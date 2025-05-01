package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class OneTimeEventSubscribersTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static OneTimeEventSubscribers getOneTimeEventSubscribersSample1() {
        return new OneTimeEventSubscribers().id(1L);
    }

    public static OneTimeEventSubscribers getOneTimeEventSubscribersSample2() {
        return new OneTimeEventSubscribers().id(2L);
    }

    public static OneTimeEventSubscribers getOneTimeEventSubscribersRandomSampleGenerator() {
        return new OneTimeEventSubscribers().id(longCount.incrementAndGet());
    }
}
