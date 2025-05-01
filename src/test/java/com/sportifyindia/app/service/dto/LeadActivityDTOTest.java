package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LeadActivityDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(LeadActivityDTO.class);
        LeadActivityDTO leadActivityDTO1 = new LeadActivityDTO();
        leadActivityDTO1.setId(1L);
        LeadActivityDTO leadActivityDTO2 = new LeadActivityDTO();
        assertThat(leadActivityDTO1).isNotEqualTo(leadActivityDTO2);
        leadActivityDTO2.setId(leadActivityDTO1.getId());
        assertThat(leadActivityDTO1).isEqualTo(leadActivityDTO2);
        leadActivityDTO2.setId(2L);
        assertThat(leadActivityDTO1).isNotEqualTo(leadActivityDTO2);
        leadActivityDTO1.setId(null);
        assertThat(leadActivityDTO1).isNotEqualTo(leadActivityDTO2);
    }
}
