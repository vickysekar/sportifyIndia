package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaxMasterDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaxMasterDTO.class);
        TaxMasterDTO taxMasterDTO1 = new TaxMasterDTO();
        taxMasterDTO1.setId(1L);
        TaxMasterDTO taxMasterDTO2 = new TaxMasterDTO();
        assertThat(taxMasterDTO1).isNotEqualTo(taxMasterDTO2);
        taxMasterDTO2.setId(taxMasterDTO1.getId());
        assertThat(taxMasterDTO1).isEqualTo(taxMasterDTO2);
        taxMasterDTO2.setId(2L);
        assertThat(taxMasterDTO1).isNotEqualTo(taxMasterDTO2);
        taxMasterDTO1.setId(null);
        assertThat(taxMasterDTO1).isNotEqualTo(taxMasterDTO2);
    }
}
