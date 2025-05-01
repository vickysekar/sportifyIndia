package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.FacilityTestSamples.*;
import static com.sportifyindia.app.domain.OneTimeEventSubscribersTestSamples.*;
import static com.sportifyindia.app.domain.OneTimeEventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class OneTimeEventTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OneTimeEvent.class);
        OneTimeEvent oneTimeEvent1 = getOneTimeEventSample1();
        OneTimeEvent oneTimeEvent2 = new OneTimeEvent();
        assertThat(oneTimeEvent1).isNotEqualTo(oneTimeEvent2);

        oneTimeEvent2.setId(oneTimeEvent1.getId());
        assertThat(oneTimeEvent1).isEqualTo(oneTimeEvent2);

        oneTimeEvent2 = getOneTimeEventSample2();
        assertThat(oneTimeEvent1).isNotEqualTo(oneTimeEvent2);
    }

    @Test
    void facilityTest() throws Exception {
        OneTimeEvent oneTimeEvent = getOneTimeEventRandomSampleGenerator();
        Facility facilityBack = getFacilityRandomSampleGenerator();

        oneTimeEvent.setFacility(facilityBack);
        assertThat(oneTimeEvent.getFacility()).isEqualTo(facilityBack);

        oneTimeEvent.facility(null);
        assertThat(oneTimeEvent.getFacility()).isNull();
    }

    @Test
    void oneTimeEventSubscribersTest() throws Exception {
        OneTimeEvent oneTimeEvent = getOneTimeEventRandomSampleGenerator();
        OneTimeEventSubscribers oneTimeEventSubscribersBack = getOneTimeEventSubscribersRandomSampleGenerator();

        oneTimeEvent.addOneTimeEventSubscribers(oneTimeEventSubscribersBack);
        assertThat(oneTimeEvent.getOneTimeEventSubscribers()).containsOnly(oneTimeEventSubscribersBack);
        assertThat(oneTimeEventSubscribersBack.getOneTimeEvent()).isEqualTo(oneTimeEvent);

        oneTimeEvent.removeOneTimeEventSubscribers(oneTimeEventSubscribersBack);
        assertThat(oneTimeEvent.getOneTimeEventSubscribers()).doesNotContain(oneTimeEventSubscribersBack);
        assertThat(oneTimeEventSubscribersBack.getOneTimeEvent()).isNull();

        oneTimeEvent.oneTimeEventSubscribers(new HashSet<>(Set.of(oneTimeEventSubscribersBack)));
        assertThat(oneTimeEvent.getOneTimeEventSubscribers()).containsOnly(oneTimeEventSubscribersBack);
        assertThat(oneTimeEventSubscribersBack.getOneTimeEvent()).isEqualTo(oneTimeEvent);

        oneTimeEvent.setOneTimeEventSubscribers(new HashSet<>());
        assertThat(oneTimeEvent.getOneTimeEventSubscribers()).doesNotContain(oneTimeEventSubscribersBack);
        assertThat(oneTimeEventSubscribersBack.getOneTimeEvent()).isNull();
    }
}
