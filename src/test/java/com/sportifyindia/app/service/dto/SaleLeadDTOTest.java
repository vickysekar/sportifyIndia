package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class SaleLeadDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(SaleLeadDTO.class);
        SaleLeadDTO saleLeadDTO1 = new SaleLeadDTO();
        saleLeadDTO1.setId(1L);
        SaleLeadDTO saleLeadDTO2 = new SaleLeadDTO();
        assertThat(saleLeadDTO1).isNotEqualTo(saleLeadDTO2);
        saleLeadDTO2.setId(saleLeadDTO1.getId());
        assertThat(saleLeadDTO1).isEqualTo(saleLeadDTO2);
        saleLeadDTO2.setId(2L);
        assertThat(saleLeadDTO1).isNotEqualTo(saleLeadDTO2);
        saleLeadDTO1.setId(null);
        assertThat(saleLeadDTO1).isNotEqualTo(saleLeadDTO2);
    }
}
