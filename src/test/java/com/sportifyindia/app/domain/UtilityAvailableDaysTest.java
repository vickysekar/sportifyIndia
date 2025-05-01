package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.TimeSlotsTestSamples.*;
import static com.sportifyindia.app.domain.UtilityAvailableDaysTestSamples.*;
import static com.sportifyindia.app.domain.UtilityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UtilityAvailableDaysTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtilityAvailableDays.class);
        UtilityAvailableDays utilityAvailableDays1 = getUtilityAvailableDaysSample1();
        UtilityAvailableDays utilityAvailableDays2 = new UtilityAvailableDays();
        assertThat(utilityAvailableDays1).isNotEqualTo(utilityAvailableDays2);

        utilityAvailableDays2.setId(utilityAvailableDays1.getId());
        assertThat(utilityAvailableDays1).isEqualTo(utilityAvailableDays2);

        utilityAvailableDays2 = getUtilityAvailableDaysSample2();
        assertThat(utilityAvailableDays1).isNotEqualTo(utilityAvailableDays2);
    }

    @Test
    void timeSlotsTest() throws Exception {
        UtilityAvailableDays utilityAvailableDays = getUtilityAvailableDaysRandomSampleGenerator();
        TimeSlots timeSlotsBack = getTimeSlotsRandomSampleGenerator();

        utilityAvailableDays.setTimeSlots(timeSlotsBack);
        assertThat(utilityAvailableDays.getTimeSlots()).isEqualTo(timeSlotsBack);

        utilityAvailableDays.timeSlots(null);
        assertThat(utilityAvailableDays.getTimeSlots()).isNull();
    }

    @Test
    void utilityTest() throws Exception {
        UtilityAvailableDays utilityAvailableDays = getUtilityAvailableDaysRandomSampleGenerator();
        Utility utilityBack = getUtilityRandomSampleGenerator();

        utilityAvailableDays.addUtility(utilityBack);
        assertThat(utilityAvailableDays.getUtilities()).containsOnly(utilityBack);
        assertThat(utilityBack.getUtilityAvailableDays()).containsOnly(utilityAvailableDays);

        utilityAvailableDays.removeUtility(utilityBack);
        assertThat(utilityAvailableDays.getUtilities()).doesNotContain(utilityBack);
        assertThat(utilityBack.getUtilityAvailableDays()).doesNotContain(utilityAvailableDays);

        utilityAvailableDays.utilities(new HashSet<>(Set.of(utilityBack)));
        assertThat(utilityAvailableDays.getUtilities()).containsOnly(utilityBack);
        assertThat(utilityBack.getUtilityAvailableDays()).containsOnly(utilityAvailableDays);

        utilityAvailableDays.setUtilities(new HashSet<>());
        assertThat(utilityAvailableDays.getUtilities()).doesNotContain(utilityBack);
        assertThat(utilityBack.getUtilityAvailableDays()).doesNotContain(utilityAvailableDays);
    }
}
