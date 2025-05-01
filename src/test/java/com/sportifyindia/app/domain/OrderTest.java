package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.ChargeTestSamples.*;
import static com.sportifyindia.app.domain.OrderTestSamples.*;
import static com.sportifyindia.app.domain.PaymentTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Order.class);
        Order order1 = getOrderSample1();
        Order order2 = new Order();
        assertThat(order1).isNotEqualTo(order2);

        order2.setId(order1.getId());
        assertThat(order1).isEqualTo(order2);

        order2 = getOrderSample2();
        assertThat(order1).isNotEqualTo(order2);
    }

    @Test
    void chargeTest() throws Exception {
        Order order = getOrderRandomSampleGenerator();
        Charge chargeBack = getChargeRandomSampleGenerator();

        order.addCharge(chargeBack);
        assertThat(order.getCharges()).containsOnly(chargeBack);
        assertThat(chargeBack.getOrder()).isEqualTo(order);

        order.removeCharge(chargeBack);
        assertThat(order.getCharges()).doesNotContain(chargeBack);
        assertThat(chargeBack.getOrder()).isNull();

        order.charges(new HashSet<>(Set.of(chargeBack)));
        assertThat(order.getCharges()).containsOnly(chargeBack);
        assertThat(chargeBack.getOrder()).isEqualTo(order);

        order.setCharges(new HashSet<>());
        assertThat(order.getCharges()).doesNotContain(chargeBack);
        assertThat(chargeBack.getOrder()).isNull();
    }

    @Test
    void paymentTest() throws Exception {
        Order order = getOrderRandomSampleGenerator();
        Payment paymentBack = getPaymentRandomSampleGenerator();

        order.addPayment(paymentBack);
        assertThat(order.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getOrder()).isEqualTo(order);

        order.removePayment(paymentBack);
        assertThat(order.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getOrder()).isNull();

        order.payments(new HashSet<>(Set.of(paymentBack)));
        assertThat(order.getPayments()).containsOnly(paymentBack);
        assertThat(paymentBack.getOrder()).isEqualTo(order);

        order.setPayments(new HashSet<>());
        assertThat(order.getPayments()).doesNotContain(paymentBack);
        assertThat(paymentBack.getOrder()).isNull();
    }
}
