package com.sportifyindia.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class PaymentTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Payment getPaymentSample1() {
        return new Payment()
            .id(1L)
            .currency("currency1")
            .gatewayStatus("gatewayStatus1")
            .gatewayCode("gatewayCode1")
            .paymentDesc("paymentDesc1")
            .transactionId("transactionId1")
            .token("token1")
            .paymentGateway("paymentGateway1");
    }

    public static Payment getPaymentSample2() {
        return new Payment()
            .id(2L)
            .currency("currency2")
            .gatewayStatus("gatewayStatus2")
            .gatewayCode("gatewayCode2")
            .paymentDesc("paymentDesc2")
            .transactionId("transactionId2")
            .token("token2")
            .paymentGateway("paymentGateway2");
    }

    public static Payment getPaymentRandomSampleGenerator() {
        return new Payment()
            .id(longCount.incrementAndGet())
            .currency(UUID.randomUUID().toString())
            .gatewayStatus(UUID.randomUUID().toString())
            .gatewayCode(UUID.randomUUID().toString())
            .paymentDesc(UUID.randomUUID().toString())
            .transactionId(UUID.randomUUID().toString())
            .token(UUID.randomUUID().toString())
            .paymentGateway(UUID.randomUUID().toString());
    }
}
