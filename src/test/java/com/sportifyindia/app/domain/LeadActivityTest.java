package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.LeadActivityTestSamples.*;
import static com.sportifyindia.app.domain.SaleLeadTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class LeadActivityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(LeadActivity.class);
        LeadActivity leadActivity1 = getLeadActivitySample1();
        LeadActivity leadActivity2 = new LeadActivity();
        assertThat(leadActivity1).isNotEqualTo(leadActivity2);

        leadActivity2.setId(leadActivity1.getId());
        assertThat(leadActivity1).isEqualTo(leadActivity2);

        leadActivity2 = getLeadActivitySample2();
        assertThat(leadActivity1).isNotEqualTo(leadActivity2);
    }

    @Test
    void saleLeadTest() throws Exception {
        LeadActivity leadActivity = getLeadActivityRandomSampleGenerator();
        SaleLead saleLeadBack = getSaleLeadRandomSampleGenerator();

        leadActivity.setSaleLead(saleLeadBack);
        assertThat(leadActivity.getSaleLead()).isEqualTo(saleLeadBack);

        leadActivity.saleLead(null);
        assertThat(leadActivity.getSaleLead()).isNull();
    }
}
