package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtilityAvailableDaysDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtilityAvailableDaysDTO.class);
        UtilityAvailableDaysDTO utilityAvailableDaysDTO1 = new UtilityAvailableDaysDTO();
        utilityAvailableDaysDTO1.setId(1L);
        UtilityAvailableDaysDTO utilityAvailableDaysDTO2 = new UtilityAvailableDaysDTO();
        assertThat(utilityAvailableDaysDTO1).isNotEqualTo(utilityAvailableDaysDTO2);
        utilityAvailableDaysDTO2.setId(utilityAvailableDaysDTO1.getId());
        assertThat(utilityAvailableDaysDTO1).isEqualTo(utilityAvailableDaysDTO2);
        utilityAvailableDaysDTO2.setId(2L);
        assertThat(utilityAvailableDaysDTO1).isNotEqualTo(utilityAvailableDaysDTO2);
        utilityAvailableDaysDTO1.setId(null);
        assertThat(utilityAvailableDaysDTO1).isNotEqualTo(utilityAvailableDaysDTO2);
    }
}
