package com.sportifyindia.app.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TimeSlotsDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TimeSlotsDTO.class);
        TimeSlotsDTO timeSlotsDTO1 = new TimeSlotsDTO();
        timeSlotsDTO1.setId(1L);
        TimeSlotsDTO timeSlotsDTO2 = new TimeSlotsDTO();
        assertThat(timeSlotsDTO1).isNotEqualTo(timeSlotsDTO2);
        timeSlotsDTO2.setId(timeSlotsDTO1.getId());
        assertThat(timeSlotsDTO1).isEqualTo(timeSlotsDTO2);
        timeSlotsDTO2.setId(2L);
        assertThat(timeSlotsDTO1).isNotEqualTo(timeSlotsDTO2);
        timeSlotsDTO1.setId(null);
        assertThat(timeSlotsDTO1).isNotEqualTo(timeSlotsDTO2);
    }
}
