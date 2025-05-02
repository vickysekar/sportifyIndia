package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.Facility;
import com.sportifyindia.app.domain.enumeration.EmployeeRoleEnum;
import com.sportifyindia.app.domain.enumeration.EmployeeStatusEnum;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Facility entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacilityRepository extends JpaRepository<Facility, Long> {
    @Query("select facility from Facility facility where facility.user.login = ?#{principal.username}")
    Page<Facility> findByUserIsCurrentUser(Pageable pageable);

    @Query(
        """
            select facility from Facility facility
            where facility.id in (
                select distinct cs.facility.id from CourseSubscription cs
                where cs.user.login = ?#{principal.username}
            )
        """
    )
    Page<Facility> findByUserSubscriptions(Pageable pageable);

    @Query(
        """
            select facility from Facility facility
            where facility.id in (
                select distinct fe.facility.id from FacilityEmployee fe
                where fe.user.id = :userId
                and fe.role = :role
                and fe.status = :status
            )
        """
    )
    Page<Facility> findByEmployeeRole(Long userId, EmployeeRoleEnum role, EmployeeStatusEnum status, Pageable pageable);
}
