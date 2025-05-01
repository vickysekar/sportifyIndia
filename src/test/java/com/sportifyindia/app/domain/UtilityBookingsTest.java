package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.UtilityBookingsTestSamples.*;
import static com.sportifyindia.app.domain.UtilitySlotsTestSamples.*;
import static com.sportifyindia.app.domain.UtilityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtilityBookingsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtilityBookings.class);
        UtilityBookings utilityBookings1 = getUtilityBookingsSample1();
        UtilityBookings utilityBookings2 = new UtilityBookings();
        assertThat(utilityBookings1).isNotEqualTo(utilityBookings2);

        utilityBookings2.setId(utilityBookings1.getId());
        assertThat(utilityBookings1).isEqualTo(utilityBookings2);

        utilityBookings2 = getUtilityBookingsSample2();
        assertThat(utilityBookings1).isNotEqualTo(utilityBookings2);
    }

    @Test
    void utilityTest() throws Exception {
        UtilityBookings utilityBookings = getUtilityBookingsRandomSampleGenerator();
        Utility utilityBack = getUtilityRandomSampleGenerator();

        utilityBookings.setUtility(utilityBack);
        assertThat(utilityBookings.getUtility()).isEqualTo(utilityBack);

        utilityBookings.utility(null);
        assertThat(utilityBookings.getUtility()).isNull();
    }

    @Test
    void utilitySlotsTest() throws Exception {
        UtilityBookings utilityBookings = getUtilityBookingsRandomSampleGenerator();
        UtilitySlots utilitySlotsBack = getUtilitySlotsRandomSampleGenerator();

        utilityBookings.setUtilitySlots(utilitySlotsBack);
        assertThat(utilityBookings.getUtilitySlots()).isEqualTo(utilitySlotsBack);

        utilityBookings.utilitySlots(null);
        assertThat(utilityBookings.getUtilitySlots()).isNull();
    }
}
