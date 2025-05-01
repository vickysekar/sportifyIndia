package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.OneTimeEventSubscribers;
import java.util.List;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OneTimeEventSubscribers entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OneTimeEventSubscribersRepository extends JpaRepository<OneTimeEventSubscribers, Long> {
    @Query(
        "select oneTimeEventSubscribers from OneTimeEventSubscribers oneTimeEventSubscribers where oneTimeEventSubscribers.user.login = ?#{authentication.name}"
    )
    List<OneTimeEventSubscribers> findByUserIsCurrentUser();
}
