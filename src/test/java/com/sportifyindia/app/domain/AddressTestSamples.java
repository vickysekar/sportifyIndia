package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class AddressTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Address getAddressSample1() {
        return new Address()
            .id(1L)
            .addressLine1("addressLine11")
            .addressLine2("addressLine21")
            .pincode("pincode1")
            .state("state1")
            .city("city1")
            .country("country1");
    }

    public static Address getAddressSample2() {
        return new Address()
            .id(2L)
            .addressLine1("addressLine12")
            .addressLine2("addressLine22")
            .pincode("pincode2")
            .state("state2")
            .city("city2")
            .country("country2");
    }

    public static Address getAddressRandomSampleGenerator() {
        return new Address()
            .id(longCount.incrementAndGet())
            .addressLine1(UUID.randomUUID().toString())
            .addressLine2(UUID.randomUUID().toString())
            .pincode(UUID.randomUUID().toString())
            .state(UUID.randomUUID().toString())
            .city(UUID.randomUUID().toString())
            .country(UUID.randomUUID().toString());
    }
}
