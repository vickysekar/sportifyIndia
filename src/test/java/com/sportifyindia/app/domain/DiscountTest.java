package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.DiscountTestSamples.*;
import static com.sportifyindia.app.domain.FacilityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DiscountTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Discount.class);
        Discount discount1 = getDiscountSample1();
        Discount discount2 = new Discount();
        assertThat(discount1).isNotEqualTo(discount2);

        discount2.setId(discount1.getId());
        assertThat(discount1).isEqualTo(discount2);

        discount2 = getDiscountSample2();
        assertThat(discount1).isNotEqualTo(discount2);
    }

    @Test
    void facilityTest() throws Exception {
        Discount discount = getDiscountRandomSampleGenerator();
        Facility facilityBack = getFacilityRandomSampleGenerator();

        discount.setFacility(facilityBack);
        assertThat(discount.getFacility()).isEqualTo(facilityBack);

        discount.facility(null);
        assertThat(discount.getFacility()).isNull();
    }
}
