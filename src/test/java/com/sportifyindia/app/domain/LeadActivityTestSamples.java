package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LeadActivityTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static LeadActivity getLeadActivitySample1() {
        return new LeadActivity().id(1L).activityType("activityType1").description("description1");
    }

    public static LeadActivity getLeadActivitySample2() {
        return new LeadActivity().id(2L).activityType("activityType2").description("description2");
    }

    public static LeadActivity getLeadActivityRandomSampleGenerator() {
        return new LeadActivity()
            .id(longCount.incrementAndGet())
            .activityType(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
