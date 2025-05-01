package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.SaleLead;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SaleLead entity.
 */
@SuppressWarnings("unused")
@Repository
public interface SaleLeadRepository extends JpaRepository<SaleLead, Long> {
    @Query("select saleLead from SaleLead saleLead where saleLead.user.login = ?#{authentication.name}")
    List<SaleLead> findByUserIsCurrentUser();
}
