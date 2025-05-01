package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class OneTimeEventDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(OneTimeEventDTO.class);
        OneTimeEventDTO oneTimeEventDTO1 = new OneTimeEventDTO();
        oneTimeEventDTO1.setId(1L);
        OneTimeEventDTO oneTimeEventDTO2 = new OneTimeEventDTO();
        assertThat(oneTimeEventDTO1).isNotEqualTo(oneTimeEventDTO2);
        oneTimeEventDTO2.setId(oneTimeEventDTO1.getId());
        assertThat(oneTimeEventDTO1).isEqualTo(oneTimeEventDTO2);
        oneTimeEventDTO2.setId(2L);
        assertThat(oneTimeEventDTO1).isNotEqualTo(oneTimeEventDTO2);
        oneTimeEventDTO1.setId(null);
        assertThat(oneTimeEventDTO1).isNotEqualTo(oneTimeEventDTO2);
    }
}
