package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtilityExceptionDaysDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtilityExceptionDaysDTO.class);
        UtilityExceptionDaysDTO utilityExceptionDaysDTO1 = new UtilityExceptionDaysDTO();
        utilityExceptionDaysDTO1.setId(1L);
        UtilityExceptionDaysDTO utilityExceptionDaysDTO2 = new UtilityExceptionDaysDTO();
        assertThat(utilityExceptionDaysDTO1).isNotEqualTo(utilityExceptionDaysDTO2);
        utilityExceptionDaysDTO2.setId(utilityExceptionDaysDTO1.getId());
        assertThat(utilityExceptionDaysDTO1).isEqualTo(utilityExceptionDaysDTO2);
        utilityExceptionDaysDTO2.setId(2L);
        assertThat(utilityExceptionDaysDTO1).isNotEqualTo(utilityExceptionDaysDTO2);
        utilityExceptionDaysDTO1.setId(null);
        assertThat(utilityExceptionDaysDTO1).isNotEqualTo(utilityExceptionDaysDTO2);
    }
}
