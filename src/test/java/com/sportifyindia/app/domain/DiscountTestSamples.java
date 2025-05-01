package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class DiscountTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Discount getDiscountSample1() {
        return new Discount().id(1L).discountCode("discountCode1").discountText("discountText1");
    }

    public static Discount getDiscountSample2() {
        return new Discount().id(2L).discountCode("discountCode2").discountText("discountText2");
    }

    public static Discount getDiscountRandomSampleGenerator() {
        return new Discount()
            .id(longCount.incrementAndGet())
            .discountCode(UUID.randomUUID().toString())
            .discountText(UUID.randomUUID().toString());
    }
}
