package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TaxTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Tax getTaxSample1() {
        return new Tax().id(1L).computedSlab("computedSlab1").taxType("taxType1");
    }

    public static Tax getTaxSample2() {
        return new Tax().id(2L).computedSlab("computedSlab2").taxType("taxType2");
    }

    public static Tax getTaxRandomSampleGenerator() {
        return new Tax().id(longCount.incrementAndGet()).computedSlab(UUID.randomUUID().toString()).taxType(UUID.randomUUID().toString());
    }
}
