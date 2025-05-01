package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.FacilityTestSamples.*;
import static com.sportifyindia.app.domain.TaxMasterTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class TaxMasterTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaxMaster.class);
        TaxMaster taxMaster1 = getTaxMasterSample1();
        TaxMaster taxMaster2 = new TaxMaster();
        assertThat(taxMaster1).isNotEqualTo(taxMaster2);

        taxMaster2.setId(taxMaster1.getId());
        assertThat(taxMaster1).isEqualTo(taxMaster2);

        taxMaster2 = getTaxMasterSample2();
        assertThat(taxMaster1).isNotEqualTo(taxMaster2);
    }

    @Test
    void facilityTest() throws Exception {
        TaxMaster taxMaster = getTaxMasterRandomSampleGenerator();
        Facility facilityBack = getFacilityRandomSampleGenerator();

        taxMaster.setFacility(facilityBack);
        assertThat(taxMaster.getFacility()).isEqualTo(facilityBack);

        taxMaster.facility(null);
        assertThat(taxMaster.getFacility()).isNull();
    }
}
