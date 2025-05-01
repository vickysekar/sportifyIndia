package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.TimeSlotsTestSamples.*;
import static com.sportifyindia.app.domain.UtilityBookingsTestSamples.*;
import static com.sportifyindia.app.domain.UtilitySlotsTestSamples.*;
import static com.sportifyindia.app.domain.UtilityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UtilitySlotsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtilitySlots.class);
        UtilitySlots utilitySlots1 = getUtilitySlotsSample1();
        UtilitySlots utilitySlots2 = new UtilitySlots();
        assertThat(utilitySlots1).isNotEqualTo(utilitySlots2);

        utilitySlots2.setId(utilitySlots1.getId());
        assertThat(utilitySlots1).isEqualTo(utilitySlots2);

        utilitySlots2 = getUtilitySlotsSample2();
        assertThat(utilitySlots1).isNotEqualTo(utilitySlots2);
    }

    @Test
    void utilityTest() throws Exception {
        UtilitySlots utilitySlots = getUtilitySlotsRandomSampleGenerator();
        Utility utilityBack = getUtilityRandomSampleGenerator();

        utilitySlots.setUtility(utilityBack);
        assertThat(utilitySlots.getUtility()).isEqualTo(utilityBack);

        utilitySlots.utility(null);
        assertThat(utilitySlots.getUtility()).isNull();
    }

    @Test
    void timeSlotsTest() throws Exception {
        UtilitySlots utilitySlots = getUtilitySlotsRandomSampleGenerator();
        TimeSlots timeSlotsBack = getTimeSlotsRandomSampleGenerator();

        utilitySlots.setTimeSlots(timeSlotsBack);
        assertThat(utilitySlots.getTimeSlots()).isEqualTo(timeSlotsBack);

        utilitySlots.timeSlots(null);
        assertThat(utilitySlots.getTimeSlots()).isNull();
    }

    @Test
    void utilityBookingsTest() throws Exception {
        UtilitySlots utilitySlots = getUtilitySlotsRandomSampleGenerator();
        UtilityBookings utilityBookingsBack = getUtilityBookingsRandomSampleGenerator();

        utilitySlots.addUtilityBookings(utilityBookingsBack);
        assertThat(utilitySlots.getUtilityBookings()).containsOnly(utilityBookingsBack);
        assertThat(utilityBookingsBack.getUtilitySlots()).isEqualTo(utilitySlots);

        utilitySlots.removeUtilityBookings(utilityBookingsBack);
        assertThat(utilitySlots.getUtilityBookings()).doesNotContain(utilityBookingsBack);
        assertThat(utilityBookingsBack.getUtilitySlots()).isNull();

        utilitySlots.utilityBookings(new HashSet<>(Set.of(utilityBookingsBack)));
        assertThat(utilitySlots.getUtilityBookings()).containsOnly(utilityBookingsBack);
        assertThat(utilityBookingsBack.getUtilitySlots()).isEqualTo(utilitySlots);

        utilitySlots.setUtilityBookings(new HashSet<>());
        assertThat(utilitySlots.getUtilityBookings()).doesNotContain(utilityBookingsBack);
        assertThat(utilityBookingsBack.getUtilitySlots()).isNull();
    }
}
