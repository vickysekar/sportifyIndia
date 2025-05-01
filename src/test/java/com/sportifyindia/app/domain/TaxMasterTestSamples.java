package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class TaxMasterTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static TaxMaster getTaxMasterSample1() {
        return new TaxMaster().id(1L).taxSlab("taxSlab1").taxName("taxName1").taxType("taxType1");
    }

    public static TaxMaster getTaxMasterSample2() {
        return new TaxMaster().id(2L).taxSlab("taxSlab2").taxName("taxName2").taxType("taxType2");
    }

    public static TaxMaster getTaxMasterRandomSampleGenerator() {
        return new TaxMaster()
            .id(longCount.incrementAndGet())
            .taxSlab(UUID.randomUUID().toString())
            .taxName(UUID.randomUUID().toString())
            .taxType(UUID.randomUUID().toString());
    }
}
