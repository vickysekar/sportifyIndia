package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.FacilityTestSamples.*;
import static com.sportifyindia.app.domain.UtilityAvailableDaysTestSamples.*;
import static com.sportifyindia.app.domain.UtilityBookingsTestSamples.*;
import static com.sportifyindia.app.domain.UtilityExceptionDaysTestSamples.*;
import static com.sportifyindia.app.domain.UtilitySlotsTestSamples.*;
import static com.sportifyindia.app.domain.UtilityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UtilityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Utility.class);
        Utility utility1 = getUtilitySample1();
        Utility utility2 = new Utility();
        assertThat(utility1).isNotEqualTo(utility2);

        utility2.setId(utility1.getId());
        assertThat(utility1).isEqualTo(utility2);

        utility2 = getUtilitySample2();
        assertThat(utility1).isNotEqualTo(utility2);
    }

    @Test
    void facilityTest() throws Exception {
        Utility utility = getUtilityRandomSampleGenerator();
        Facility facilityBack = getFacilityRandomSampleGenerator();

        utility.setFacility(facilityBack);
        assertThat(utility.getFacility()).isEqualTo(facilityBack);

        utility.facility(null);
        assertThat(utility.getFacility()).isNull();
    }

    @Test
    void utilityAvailableDaysTest() throws Exception {
        Utility utility = getUtilityRandomSampleGenerator();
        UtilityAvailableDays utilityAvailableDaysBack = getUtilityAvailableDaysRandomSampleGenerator();

        utility.addUtilityAvailableDays(utilityAvailableDaysBack);
        assertThat(utility.getUtilityAvailableDays()).containsOnly(utilityAvailableDaysBack);

        utility.removeUtilityAvailableDays(utilityAvailableDaysBack);
        assertThat(utility.getUtilityAvailableDays()).doesNotContain(utilityAvailableDaysBack);

        utility.utilityAvailableDays(new HashSet<>(Set.of(utilityAvailableDaysBack)));
        assertThat(utility.getUtilityAvailableDays()).containsOnly(utilityAvailableDaysBack);

        utility.setUtilityAvailableDays(new HashSet<>());
        assertThat(utility.getUtilityAvailableDays()).doesNotContain(utilityAvailableDaysBack);
    }

    @Test
    void utilityExceptionDaysTest() throws Exception {
        Utility utility = getUtilityRandomSampleGenerator();
        UtilityExceptionDays utilityExceptionDaysBack = getUtilityExceptionDaysRandomSampleGenerator();

        utility.addUtilityExceptionDays(utilityExceptionDaysBack);
        assertThat(utility.getUtilityExceptionDays()).containsOnly(utilityExceptionDaysBack);
        assertThat(utilityExceptionDaysBack.getUtility()).isEqualTo(utility);

        utility.removeUtilityExceptionDays(utilityExceptionDaysBack);
        assertThat(utility.getUtilityExceptionDays()).doesNotContain(utilityExceptionDaysBack);
        assertThat(utilityExceptionDaysBack.getUtility()).isNull();

        utility.utilityExceptionDays(new HashSet<>(Set.of(utilityExceptionDaysBack)));
        assertThat(utility.getUtilityExceptionDays()).containsOnly(utilityExceptionDaysBack);
        assertThat(utilityExceptionDaysBack.getUtility()).isEqualTo(utility);

        utility.setUtilityExceptionDays(new HashSet<>());
        assertThat(utility.getUtilityExceptionDays()).doesNotContain(utilityExceptionDaysBack);
        assertThat(utilityExceptionDaysBack.getUtility()).isNull();
    }

    @Test
    void utilitySlotsTest() throws Exception {
        Utility utility = getUtilityRandomSampleGenerator();
        UtilitySlots utilitySlotsBack = getUtilitySlotsRandomSampleGenerator();

        utility.addUtilitySlots(utilitySlotsBack);
        assertThat(utility.getUtilitySlots()).containsOnly(utilitySlotsBack);
        assertThat(utilitySlotsBack.getUtility()).isEqualTo(utility);

        utility.removeUtilitySlots(utilitySlotsBack);
        assertThat(utility.getUtilitySlots()).doesNotContain(utilitySlotsBack);
        assertThat(utilitySlotsBack.getUtility()).isNull();

        utility.utilitySlots(new HashSet<>(Set.of(utilitySlotsBack)));
        assertThat(utility.getUtilitySlots()).containsOnly(utilitySlotsBack);
        assertThat(utilitySlotsBack.getUtility()).isEqualTo(utility);

        utility.setUtilitySlots(new HashSet<>());
        assertThat(utility.getUtilitySlots()).doesNotContain(utilitySlotsBack);
        assertThat(utilitySlotsBack.getUtility()).isNull();
    }

    @Test
    void utilityBookingsTest() throws Exception {
        Utility utility = getUtilityRandomSampleGenerator();
        UtilityBookings utilityBookingsBack = getUtilityBookingsRandomSampleGenerator();

        utility.addUtilityBookings(utilityBookingsBack);
        assertThat(utility.getUtilityBookings()).containsOnly(utilityBookingsBack);
        assertThat(utilityBookingsBack.getUtility()).isEqualTo(utility);

        utility.removeUtilityBookings(utilityBookingsBack);
        assertThat(utility.getUtilityBookings()).doesNotContain(utilityBookingsBack);
        assertThat(utilityBookingsBack.getUtility()).isNull();

        utility.utilityBookings(new HashSet<>(Set.of(utilityBookingsBack)));
        assertThat(utility.getUtilityBookings()).containsOnly(utilityBookingsBack);
        assertThat(utilityBookingsBack.getUtility()).isEqualTo(utility);

        utility.setUtilityBookings(new HashSet<>());
        assertThat(utility.getUtilityBookings()).doesNotContain(utilityBookingsBack);
        assertThat(utilityBookingsBack.getUtility()).isNull();
    }
}
