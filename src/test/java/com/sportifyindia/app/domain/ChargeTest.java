package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.ChargeTestSamples.*;
import static com.sportifyindia.app.domain.OrderTestSamples.*;
import static com.sportifyindia.app.domain.TaxTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ChargeTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Charge.class);
        Charge charge1 = getChargeSample1();
        Charge charge2 = new Charge();
        assertThat(charge1).isNotEqualTo(charge2);

        charge2.setId(charge1.getId());
        assertThat(charge1).isEqualTo(charge2);

        charge2 = getChargeSample2();
        assertThat(charge1).isNotEqualTo(charge2);
    }

    @Test
    void orderTest() throws Exception {
        Charge charge = getChargeRandomSampleGenerator();
        Order orderBack = getOrderRandomSampleGenerator();

        charge.setOrder(orderBack);
        assertThat(charge.getOrder()).isEqualTo(orderBack);

        charge.order(null);
        assertThat(charge.getOrder()).isNull();
    }

    @Test
    void taxTest() throws Exception {
        Charge charge = getChargeRandomSampleGenerator();
        Tax taxBack = getTaxRandomSampleGenerator();

        charge.addTax(taxBack);
        assertThat(charge.getTaxes()).containsOnly(taxBack);
        assertThat(taxBack.getCharge()).isEqualTo(charge);

        charge.removeTax(taxBack);
        assertThat(charge.getTaxes()).doesNotContain(taxBack);
        assertThat(taxBack.getCharge()).isNull();

        charge.taxes(new HashSet<>(Set.of(taxBack)));
        assertThat(charge.getTaxes()).containsOnly(taxBack);
        assertThat(taxBack.getCharge()).isEqualTo(charge);

        charge.setTaxes(new HashSet<>());
        assertThat(charge.getTaxes()).doesNotContain(taxBack);
        assertThat(taxBack.getCharge()).isNull();
    }
}
