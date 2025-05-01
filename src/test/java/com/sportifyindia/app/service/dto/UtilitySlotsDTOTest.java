package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtilitySlotsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtilitySlotsDTO.class);
        UtilitySlotsDTO utilitySlotsDTO1 = new UtilitySlotsDTO();
        utilitySlotsDTO1.setId(1L);
        UtilitySlotsDTO utilitySlotsDTO2 = new UtilitySlotsDTO();
        assertThat(utilitySlotsDTO1).isNotEqualTo(utilitySlotsDTO2);
        utilitySlotsDTO2.setId(utilitySlotsDTO1.getId());
        assertThat(utilitySlotsDTO1).isEqualTo(utilitySlotsDTO2);
        utilitySlotsDTO2.setId(2L);
        assertThat(utilitySlotsDTO1).isNotEqualTo(utilitySlotsDTO2);
        utilitySlotsDTO1.setId(null);
        assertThat(utilitySlotsDTO1).isNotEqualTo(utilitySlotsDTO2);
    }
}
