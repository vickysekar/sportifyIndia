package com.sportifyindia.app.domain;

import static com.sportifyindia.app.domain.AddressTestSamples.*;
import static com.sportifyindia.app.domain.FacilityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.sportifyindia.app.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Address.class);
        Address address1 = getAddressSample1();
        Address address2 = new Address();
        assertThat(address1).isNotEqualTo(address2);

        address2.setId(address1.getId());
        assertThat(address1).isEqualTo(address2);

        address2 = getAddressSample2();
        assertThat(address1).isNotEqualTo(address2);
    }

    @Test
    void facilityTest() throws Exception {
        Address address = getAddressRandomSampleGenerator();
        Facility facilityBack = getFacilityRandomSampleGenerator();

        address.setFacility(facilityBack);
        assertThat(address.getFacility()).isEqualTo(facilityBack);

        address.facility(null);
        assertThat(address.getFacility()).isNull();
    }
}
