package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.UtilityExceptionDaysTestSamples.*;
import static com.sportifyindia.app.domain.UtilityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class UtilityExceptionDaysTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UtilityExceptionDays.class);
        UtilityExceptionDays utilityExceptionDays1 = getUtilityExceptionDaysSample1();
        UtilityExceptionDays utilityExceptionDays2 = new UtilityExceptionDays();
        assertThat(utilityExceptionDays1).isNotEqualTo(utilityExceptionDays2);

        utilityExceptionDays2.setId(utilityExceptionDays1.getId());
        assertThat(utilityExceptionDays1).isEqualTo(utilityExceptionDays2);

        utilityExceptionDays2 = getUtilityExceptionDaysSample2();
        assertThat(utilityExceptionDays1).isNotEqualTo(utilityExceptionDays2);
    }

    @Test
    void utilityTest() throws Exception {
        UtilityExceptionDays utilityExceptionDays = getUtilityExceptionDaysRandomSampleGenerator();
        Utility utilityBack = getUtilityRandomSampleGenerator();

        utilityExceptionDays.setUtility(utilityBack);
        assertThat(utilityExceptionDays.getUtility()).isEqualTo(utilityBack);

        utilityExceptionDays.utility(null);
        assertThat(utilityExceptionDays.getUtility()).isNull();
    }
}
