package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.Charge;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Charge entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ChargeRepository extends JpaRepository<Charge, Long> {
    @Query("select charge from Charge charge where charge.user.login = ?#{authentication.name}")
    List<Charge> findByUserIsCurrentUser();
}
