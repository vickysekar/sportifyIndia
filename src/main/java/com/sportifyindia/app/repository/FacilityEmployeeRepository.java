package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.FacilityEmployee;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FacilityEmployee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacilityEmployeeRepository extends JpaRepository<FacilityEmployee, Long> {}
