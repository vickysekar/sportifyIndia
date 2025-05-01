package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OneTimeEventSubscribersDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OneTimeEventSubscribersDTO.class);
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO1 = new OneTimeEventSubscribersDTO();
        oneTimeEventSubscribersDTO1.setId(1L);
        OneTimeEventSubscribersDTO oneTimeEventSubscribersDTO2 = new OneTimeEventSubscribersDTO();
        assertThat(oneTimeEventSubscribersDTO1).isNotEqualTo(oneTimeEventSubscribersDTO2);
        oneTimeEventSubscribersDTO2.setId(oneTimeEventSubscribersDTO1.getId());
        assertThat(oneTimeEventSubscribersDTO1).isEqualTo(oneTimeEventSubscribersDTO2);
        oneTimeEventSubscribersDTO2.setId(2L);
        assertThat(oneTimeEventSubscribersDTO1).isNotEqualTo(oneTimeEventSubscribersDTO2);
        oneTimeEventSubscribersDTO1.setId(null);
        assertThat(oneTimeEventSubscribersDTO1).isNotEqualTo(oneTimeEventSubscribersDTO2);
    }
}
