package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.TaxMaster;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the TaxMaster entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TaxMasterRepository extends JpaRepository<TaxMaster, Long> {}
