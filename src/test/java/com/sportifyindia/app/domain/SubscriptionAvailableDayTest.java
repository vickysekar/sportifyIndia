package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.SubscriptionAvailableDayTestSamples.*;
import static com.sportifyindia.app.domain.SubscriptionPlanTestSamples.*;
import static com.sportifyindia.app.domain.TimeSlotsTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SubscriptionAvailableDayTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubscriptionAvailableDay.class);
        SubscriptionAvailableDay subscriptionAvailableDay1 = getSubscriptionAvailableDaySample1();
        SubscriptionAvailableDay subscriptionAvailableDay2 = new SubscriptionAvailableDay();
        assertThat(subscriptionAvailableDay1).isNotEqualTo(subscriptionAvailableDay2);

        subscriptionAvailableDay2.setId(subscriptionAvailableDay1.getId());
        assertThat(subscriptionAvailableDay1).isEqualTo(subscriptionAvailableDay2);

        subscriptionAvailableDay2 = getSubscriptionAvailableDaySample2();
        assertThat(subscriptionAvailableDay1).isNotEqualTo(subscriptionAvailableDay2);
    }

    @Test
    void timeSlotsTest() throws Exception {
        SubscriptionAvailableDay subscriptionAvailableDay = getSubscriptionAvailableDayRandomSampleGenerator();
        TimeSlots timeSlotsBack = getTimeSlotsRandomSampleGenerator();

        subscriptionAvailableDay.setTimeSlots(timeSlotsBack);
        assertThat(subscriptionAvailableDay.getTimeSlots()).isEqualTo(timeSlotsBack);

        subscriptionAvailableDay.timeSlots(null);
        assertThat(subscriptionAvailableDay.getTimeSlots()).isNull();
    }

    @Test
    void subscriptionPlanTest() throws Exception {
        SubscriptionAvailableDay subscriptionAvailableDay = getSubscriptionAvailableDayRandomSampleGenerator();
        SubscriptionPlan subscriptionPlanBack = getSubscriptionPlanRandomSampleGenerator();

        subscriptionAvailableDay.addSubscriptionPlan(subscriptionPlanBack);
        assertThat(subscriptionAvailableDay.getSubscriptionPlans()).containsOnly(subscriptionPlanBack);
        assertThat(subscriptionPlanBack.getSubscriptionAvailableDays()).containsOnly(subscriptionAvailableDay);

        subscriptionAvailableDay.removeSubscriptionPlan(subscriptionPlanBack);
        assertThat(subscriptionAvailableDay.getSubscriptionPlans()).doesNotContain(subscriptionPlanBack);
        assertThat(subscriptionPlanBack.getSubscriptionAvailableDays()).doesNotContain(subscriptionAvailableDay);

        subscriptionAvailableDay.subscriptionPlans(new HashSet<>(Set.of(subscriptionPlanBack)));
        assertThat(subscriptionAvailableDay.getSubscriptionPlans()).containsOnly(subscriptionPlanBack);
        assertThat(subscriptionPlanBack.getSubscriptionAvailableDays()).containsOnly(subscriptionAvailableDay);

        subscriptionAvailableDay.setSubscriptionPlans(new HashSet<>());
        assertThat(subscriptionAvailableDay.getSubscriptionPlans()).doesNotContain(subscriptionPlanBack);
        assertThat(subscriptionPlanBack.getSubscriptionAvailableDays()).doesNotContain(subscriptionAvailableDay);
    }
}
