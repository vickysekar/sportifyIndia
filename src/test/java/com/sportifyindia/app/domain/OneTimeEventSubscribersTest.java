package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.OneTimeEventSubscribersTestSamples.*;
import static com.sportifyindia.app.domain.OneTimeEventTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OneTimeEventSubscribersTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(OneTimeEventSubscribers.class);
        OneTimeEventSubscribers oneTimeEventSubscribers1 = getOneTimeEventSubscribersSample1();
        OneTimeEventSubscribers oneTimeEventSubscribers2 = new OneTimeEventSubscribers();
        assertThat(oneTimeEventSubscribers1).isNotEqualTo(oneTimeEventSubscribers2);

        oneTimeEventSubscribers2.setId(oneTimeEventSubscribers1.getId());
        assertThat(oneTimeEventSubscribers1).isEqualTo(oneTimeEventSubscribers2);

        oneTimeEventSubscribers2 = getOneTimeEventSubscribersSample2();
        assertThat(oneTimeEventSubscribers1).isNotEqualTo(oneTimeEventSubscribers2);
    }

    @Test
    void oneTimeEventTest() throws Exception {
        OneTimeEventSubscribers oneTimeEventSubscribers = getOneTimeEventSubscribersRandomSampleGenerator();
        OneTimeEvent oneTimeEventBack = getOneTimeEventRandomSampleGenerator();

        oneTimeEventSubscribers.setOneTimeEvent(oneTimeEventBack);
        assertThat(oneTimeEventSubscribers.getOneTimeEvent()).isEqualTo(oneTimeEventBack);

        oneTimeEventSubscribers.oneTimeEvent(null);
        assertThat(oneTimeEventSubscribers.getOneTimeEvent()).isNull();
    }
}
