package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class OneTimeEventTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static OneTimeEvent getOneTimeEventSample1() {
        return new OneTimeEvent()
            .id(1L)
            .eventName("eventName1")
            .eventDesc("eventDesc1")
            .maxCapacity(1)
            .imageLinks("imageLinks1")
            .category("category1")
            .tags("tags1")
            .termsAndConditions("termsAndConditions1");
    }

    public static OneTimeEvent getOneTimeEventSample2() {
        return new OneTimeEvent()
            .id(2L)
            .eventName("eventName2")
            .eventDesc("eventDesc2")
            .maxCapacity(2)
            .imageLinks("imageLinks2")
            .category("category2")
            .tags("tags2")
            .termsAndConditions("termsAndConditions2");
    }

    public static OneTimeEvent getOneTimeEventRandomSampleGenerator() {
        return new OneTimeEvent()
            .id(longCount.incrementAndGet())
            .eventName(UUID.randomUUID().toString())
            .eventDesc(UUID.randomUUID().toString())
            .maxCapacity(intCount.incrementAndGet())
            .imageLinks(UUID.randomUUID().toString())
            .category(UUID.randomUUID().toString())
            .tags(UUID.randomUUID().toString())
            .termsAndConditions(UUID.randomUUID().toString());
    }
}
