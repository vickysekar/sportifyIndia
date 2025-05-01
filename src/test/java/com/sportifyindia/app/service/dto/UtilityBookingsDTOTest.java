package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtilityBookingsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtilityBookingsDTO.class);
        UtilityBookingsDTO utilityBookingsDTO1 = new UtilityBookingsDTO();
        utilityBookingsDTO1.setId(1L);
        UtilityBookingsDTO utilityBookingsDTO2 = new UtilityBookingsDTO();
        assertThat(utilityBookingsDTO1).isNotEqualTo(utilityBookingsDTO2);
        utilityBookingsDTO2.setId(utilityBookingsDTO1.getId());
        assertThat(utilityBookingsDTO1).isEqualTo(utilityBookingsDTO2);
        utilityBookingsDTO2.setId(2L);
        assertThat(utilityBookingsDTO1).isNotEqualTo(utilityBookingsDTO2);
        utilityBookingsDTO1.setId(null);
        assertThat(utilityBookingsDTO1).isNotEqualTo(utilityBookingsDTO2);
    }
}
