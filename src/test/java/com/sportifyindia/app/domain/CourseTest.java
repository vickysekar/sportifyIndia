package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.CourseTestSamples.*;
import static com.sportifyindia.app.domain.FacilityTestSamples.*;
import static com.sportifyindia.app.domain.SubscriptionPlanTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class CourseTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Course.class);
        Course course1 = getCourseSample1();
        Course course2 = new Course();
        assertThat(course1).isNotEqualTo(course2);

        course2.setId(course1.getId());
        assertThat(course1).isEqualTo(course2);

        course2 = getCourseSample2();
        assertThat(course1).isNotEqualTo(course2);
    }

    @Test
    void facilityTest() throws Exception {
        Course course = getCourseRandomSampleGenerator();
        Facility facilityBack = getFacilityRandomSampleGenerator();

        course.setFacility(facilityBack);
        assertThat(course.getFacility()).isEqualTo(facilityBack);

        course.facility(null);
        assertThat(course.getFacility()).isNull();
    }

    @Test
    void subscriptionPlanTest() throws Exception {
        Course course = getCourseRandomSampleGenerator();
        SubscriptionPlan subscriptionPlanBack = getSubscriptionPlanRandomSampleGenerator();

        course.addSubscriptionPlan(subscriptionPlanBack);
        assertThat(course.getSubscriptionPlans()).containsOnly(subscriptionPlanBack);
        assertThat(subscriptionPlanBack.getCourse()).isEqualTo(course);

        course.removeSubscriptionPlan(subscriptionPlanBack);
        assertThat(course.getSubscriptionPlans()).doesNotContain(subscriptionPlanBack);
        assertThat(subscriptionPlanBack.getCourse()).isNull();

        course.subscriptionPlans(new HashSet<>(Set.of(subscriptionPlanBack)));
        assertThat(course.getSubscriptionPlans()).containsOnly(subscriptionPlanBack);
        assertThat(subscriptionPlanBack.getCourse()).isEqualTo(course);

        course.setSubscriptionPlans(new HashSet<>());
        assertThat(course.getSubscriptionPlans()).doesNotContain(subscriptionPlanBack);
        assertThat(subscriptionPlanBack.getCourse()).isNull();
    }
}
