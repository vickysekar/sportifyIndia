package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class FacilityEmployeeDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(FacilityEmployeeDTO.class);
        FacilityEmployeeDTO facilityEmployeeDTO1 = new FacilityEmployeeDTO();
        facilityEmployeeDTO1.setId(1L);
        FacilityEmployeeDTO facilityEmployeeDTO2 = new FacilityEmployeeDTO();
        assertThat(facilityEmployeeDTO1).isNotEqualTo(facilityEmployeeDTO2);
        facilityEmployeeDTO2.setId(facilityEmployeeDTO1.getId());
        assertThat(facilityEmployeeDTO1).isEqualTo(facilityEmployeeDTO2);
        facilityEmployeeDTO2.setId(2L);
        assertThat(facilityEmployeeDTO1).isNotEqualTo(facilityEmployeeDTO2);
        facilityEmployeeDTO1.setId(null);
        assertThat(facilityEmployeeDTO1).isNotEqualTo(facilityEmployeeDTO2);
    }
}
