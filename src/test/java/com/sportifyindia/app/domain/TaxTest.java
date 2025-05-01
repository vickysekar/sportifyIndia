package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.ChargeTestSamples.*;
import static com.sportifyindia.app.domain.TaxTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaxTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tax.class);
        Tax tax1 = getTaxSample1();
        Tax tax2 = new Tax();
        assertThat(tax1).isNotEqualTo(tax2);

        tax2.setId(tax1.getId());
        assertThat(tax1).isEqualTo(tax2);

        tax2 = getTaxSample2();
        assertThat(tax1).isNotEqualTo(tax2);
    }

    @Test
    void chargeTest() throws Exception {
        Tax tax = getTaxRandomSampleGenerator();
        Charge chargeBack = getChargeRandomSampleGenerator();

        tax.setCharge(chargeBack);
        assertThat(tax.getCharge()).isEqualTo(chargeBack);

        tax.charge(null);
        assertThat(tax.getCharge()).isNull();
    }
}
