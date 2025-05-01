package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.SubscriptionAvailableDayTestSamples.*;
import static com.sportifyindia.app.domain.TimeSlotsTestSamples.*;
import static com.sportifyindia.app.domain.UtilityAvailableDaysTestSamples.*;
import static com.sportifyindia.app.domain.UtilitySlotsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TimeSlotsTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeSlots.class);
        TimeSlots timeSlots1 = getTimeSlotsSample1();
        TimeSlots timeSlots2 = new TimeSlots();
        assertThat(timeSlots1).isNotEqualTo(timeSlots2);

        timeSlots2.setId(timeSlots1.getId());
        assertThat(timeSlots1).isEqualTo(timeSlots2);

        timeSlots2 = getTimeSlotsSample2();
        assertThat(timeSlots1).isNotEqualTo(timeSlots2);
    }

    @Test
    void subscriptionAvailableDayTest() throws Exception {
        TimeSlots timeSlots = getTimeSlotsRandomSampleGenerator();
        SubscriptionAvailableDay subscriptionAvailableDayBack = getSubscriptionAvailableDayRandomSampleGenerator();

        timeSlots.addSubscriptionAvailableDay(subscriptionAvailableDayBack);
        assertThat(timeSlots.getSubscriptionAvailableDays()).containsOnly(subscriptionAvailableDayBack);
        assertThat(subscriptionAvailableDayBack.getTimeSlots()).isEqualTo(timeSlots);

        timeSlots.removeSubscriptionAvailableDay(subscriptionAvailableDayBack);
        assertThat(timeSlots.getSubscriptionAvailableDays()).doesNotContain(subscriptionAvailableDayBack);
        assertThat(subscriptionAvailableDayBack.getTimeSlots()).isNull();

        timeSlots.subscriptionAvailableDays(new HashSet<>(Set.of(subscriptionAvailableDayBack)));
        assertThat(timeSlots.getSubscriptionAvailableDays()).containsOnly(subscriptionAvailableDayBack);
        assertThat(subscriptionAvailableDayBack.getTimeSlots()).isEqualTo(timeSlots);

        timeSlots.setSubscriptionAvailableDays(new HashSet<>());
        assertThat(timeSlots.getSubscriptionAvailableDays()).doesNotContain(subscriptionAvailableDayBack);
        assertThat(subscriptionAvailableDayBack.getTimeSlots()).isNull();
    }

    @Test
    void utilityAvailableDaysTest() throws Exception {
        TimeSlots timeSlots = getTimeSlotsRandomSampleGenerator();
        UtilityAvailableDays utilityAvailableDaysBack = getUtilityAvailableDaysRandomSampleGenerator();

        timeSlots.addUtilityAvailableDays(utilityAvailableDaysBack);
        assertThat(timeSlots.getUtilityAvailableDays()).containsOnly(utilityAvailableDaysBack);
        assertThat(utilityAvailableDaysBack.getTimeSlots()).isEqualTo(timeSlots);

        timeSlots.removeUtilityAvailableDays(utilityAvailableDaysBack);
        assertThat(timeSlots.getUtilityAvailableDays()).doesNotContain(utilityAvailableDaysBack);
        assertThat(utilityAvailableDaysBack.getTimeSlots()).isNull();

        timeSlots.utilityAvailableDays(new HashSet<>(Set.of(utilityAvailableDaysBack)));
        assertThat(timeSlots.getUtilityAvailableDays()).containsOnly(utilityAvailableDaysBack);
        assertThat(utilityAvailableDaysBack.getTimeSlots()).isEqualTo(timeSlots);

        timeSlots.setUtilityAvailableDays(new HashSet<>());
        assertThat(timeSlots.getUtilityAvailableDays()).doesNotContain(utilityAvailableDaysBack);
        assertThat(utilityAvailableDaysBack.getTimeSlots()).isNull();
    }

    @Test
    void utilitySlotsTest() throws Exception {
        TimeSlots timeSlots = getTimeSlotsRandomSampleGenerator();
        UtilitySlots utilitySlotsBack = getUtilitySlotsRandomSampleGenerator();

        timeSlots.addUtilitySlots(utilitySlotsBack);
        assertThat(timeSlots.getUtilitySlots()).containsOnly(utilitySlotsBack);
        assertThat(utilitySlotsBack.getTimeSlots()).isEqualTo(timeSlots);

        timeSlots.removeUtilitySlots(utilitySlotsBack);
        assertThat(timeSlots.getUtilitySlots()).doesNotContain(utilitySlotsBack);
        assertThat(utilitySlotsBack.getTimeSlots()).isNull();

        timeSlots.utilitySlots(new HashSet<>(Set.of(utilitySlotsBack)));
        assertThat(timeSlots.getUtilitySlots()).containsOnly(utilitySlotsBack);
        assertThat(utilitySlotsBack.getTimeSlots()).isEqualTo(timeSlots);

        timeSlots.setUtilitySlots(new HashSet<>());
        assertThat(timeSlots.getUtilitySlots()).doesNotContain(utilitySlotsBack);
        assertThat(utilitySlotsBack.getTimeSlots()).isNull();
    }
}
