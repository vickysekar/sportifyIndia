package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.FacilityEmployee;
import com.sportifyindia.app.domain.enumeration.EmployeeRoleEnum;
import com.sportifyindia.app.domain.enumeration.EmployeeStatusEnum;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the FacilityEmployee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FacilityEmployeeRepository extends JpaRepository<FacilityEmployee, Long> {
    @Query(
        """
            select count(fe) > 0 from FacilityEmployee fe
            where fe.facility.id = :facilityId
            and fe.user.id = :userId
            and fe.role = :role
            and fe.status = :status
        """
    )
    boolean existsByFacilityIdAndUserIdAndRoleAndStatus(Long facilityId, Long userId, EmployeeRoleEnum role, EmployeeStatusEnum status);

    @Query(
        """
            select count(fe) > 0 from FacilityEmployee fe
            where fe.facility.id = :facilityId
            and fe.user.id = :userId
            and fe.status = :status
        """
    )
    boolean existsByFacilityIdAndUserIdAndStatus(Long facilityId, Long userId, EmployeeStatusEnum status);

    @Query(
        """
            select fe from FacilityEmployee fe
            where fe.facility.id = :facilityId
        """
    )
    Page<FacilityEmployee> findByFacilityId(Long facilityId, Pageable pageable);

    @Query(
        """
            select fe from FacilityEmployee fe
            where fe.id = :id
            and fe.facility.id = :facilityId
        """
    )
    Optional<FacilityEmployee> findByIdAndFacilityId(Long id, Long facilityId);
}
