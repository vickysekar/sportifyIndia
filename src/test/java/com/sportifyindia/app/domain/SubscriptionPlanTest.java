package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.CourseTestSamples.*;
import static com.sportifyindia.app.domain.SubscriptionAvailableDayTestSamples.*;
import static com.sportifyindia.app.domain.SubscriptionPlanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class SubscriptionPlanTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubscriptionPlan.class);
        SubscriptionPlan subscriptionPlan1 = getSubscriptionPlanSample1();
        SubscriptionPlan subscriptionPlan2 = new SubscriptionPlan();
        assertThat(subscriptionPlan1).isNotEqualTo(subscriptionPlan2);

        subscriptionPlan2.setId(subscriptionPlan1.getId());
        assertThat(subscriptionPlan1).isEqualTo(subscriptionPlan2);

        subscriptionPlan2 = getSubscriptionPlanSample2();
        assertThat(subscriptionPlan1).isNotEqualTo(subscriptionPlan2);
    }

    @Test
    void courseTest() throws Exception {
        SubscriptionPlan subscriptionPlan = getSubscriptionPlanRandomSampleGenerator();
        Course courseBack = getCourseRandomSampleGenerator();

        subscriptionPlan.setCourse(courseBack);
        assertThat(subscriptionPlan.getCourse()).isEqualTo(courseBack);

        subscriptionPlan.course(null);
        assertThat(subscriptionPlan.getCourse()).isNull();
    }

    @Test
    void subscriptionAvailableDayTest() throws Exception {
        SubscriptionPlan subscriptionPlan = getSubscriptionPlanRandomSampleGenerator();
        SubscriptionAvailableDay subscriptionAvailableDayBack = getSubscriptionAvailableDayRandomSampleGenerator();

        subscriptionPlan.addSubscriptionAvailableDay(subscriptionAvailableDayBack);
        assertThat(subscriptionPlan.getSubscriptionAvailableDays()).containsOnly(subscriptionAvailableDayBack);

        subscriptionPlan.removeSubscriptionAvailableDay(subscriptionAvailableDayBack);
        assertThat(subscriptionPlan.getSubscriptionAvailableDays()).doesNotContain(subscriptionAvailableDayBack);

        subscriptionPlan.subscriptionAvailableDays(new HashSet<>(Set.of(subscriptionAvailableDayBack)));
        assertThat(subscriptionPlan.getSubscriptionAvailableDays()).containsOnly(subscriptionAvailableDayBack);

        subscriptionPlan.setSubscriptionAvailableDays(new HashSet<>());
        assertThat(subscriptionPlan.getSubscriptionAvailableDays()).doesNotContain(subscriptionAvailableDayBack);
    }
}
