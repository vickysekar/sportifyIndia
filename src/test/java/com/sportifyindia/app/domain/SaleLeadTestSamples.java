package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class SaleLeadTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static SaleLead getSaleLeadSample1() {
        return new SaleLead()
            .id(1L)
            .fullName("fullName1")
            .lastName("lastName1")
            .email("email1")
            .phoneNumber("phoneNumber1")
            .title("title1")
            .leadSource("leadSource1")
            .description("description1");
    }

    public static SaleLead getSaleLeadSample2() {
        return new SaleLead()
            .id(2L)
            .fullName("fullName2")
            .lastName("lastName2")
            .email("email2")
            .phoneNumber("phoneNumber2")
            .title("title2")
            .leadSource("leadSource2")
            .description("description2");
    }

    public static SaleLead getSaleLeadRandomSampleGenerator() {
        return new SaleLead()
            .id(longCount.incrementAndGet())
            .fullName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString())
            .title(UUID.randomUUID().toString())
            .leadSource(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString());
    }
}
