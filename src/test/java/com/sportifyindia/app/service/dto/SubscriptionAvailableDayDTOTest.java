package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SubscriptionAvailableDayDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SubscriptionAvailableDayDTO.class);
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO1 = new SubscriptionAvailableDayDTO();
        subscriptionAvailableDayDTO1.setId(1L);
        SubscriptionAvailableDayDTO subscriptionAvailableDayDTO2 = new SubscriptionAvailableDayDTO();
        assertThat(subscriptionAvailableDayDTO1).isNotEqualTo(subscriptionAvailableDayDTO2);
        subscriptionAvailableDayDTO2.setId(subscriptionAvailableDayDTO1.getId());
        assertThat(subscriptionAvailableDayDTO1).isEqualTo(subscriptionAvailableDayDTO2);
        subscriptionAvailableDayDTO2.setId(2L);
        assertThat(subscriptionAvailableDayDTO1).isNotEqualTo(subscriptionAvailableDayDTO2);
        subscriptionAvailableDayDTO1.setId(null);
        assertThat(subscriptionAvailableDayDTO1).isNotEqualTo(subscriptionAvailableDayDTO2);
    }
}
