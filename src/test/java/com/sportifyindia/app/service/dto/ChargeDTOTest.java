package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ChargeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ChargeDTO.class);
        ChargeDTO chargeDTO1 = new ChargeDTO();
        chargeDTO1.setId(1L);
        ChargeDTO chargeDTO2 = new ChargeDTO();
        assertThat(chargeDTO1).isNotEqualTo(chargeDTO2);
        chargeDTO2.setId(chargeDTO1.getId());
        assertThat(chargeDTO1).isEqualTo(chargeDTO2);
        chargeDTO2.setId(2L);
        assertThat(chargeDTO1).isNotEqualTo(chargeDTO2);
        chargeDTO1.setId(null);
        assertThat(chargeDTO1).isNotEqualTo(chargeDTO2);
    }
}
