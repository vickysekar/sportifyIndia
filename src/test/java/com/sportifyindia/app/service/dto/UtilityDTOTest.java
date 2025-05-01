package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtilityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtilityDTO.class);
        UtilityDTO utilityDTO1 = new UtilityDTO();
        utilityDTO1.setId(1L);
        UtilityDTO utilityDTO2 = new UtilityDTO();
        assertThat(utilityDTO1).isNotEqualTo(utilityDTO2);
        utilityDTO2.setId(utilityDTO1.getId());
        assertThat(utilityDTO1).isEqualTo(utilityDTO2);
        utilityDTO2.setId(2L);
        assertThat(utilityDTO1).isNotEqualTo(utilityDTO2);
        utilityDTO1.setId(null);
        assertThat(utilityDTO1).isNotEqualTo(utilityDTO2);
    }
}
