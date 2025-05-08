package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.UtilityBookings;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UtilityBookings entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UtilityBookingsRepository extends JpaRepository<UtilityBookings, Long> {
    @Query("SELECT b FROM UtilityBookings b WHERE b.user.login = :userLogin")
    Page<UtilityBookings> findByUserLogin(@Param("userLogin") String userLogin, Pageable pageable);

    @Query(
        "SELECT b FROM UtilityBookings b WHERE b.utilitySlots.utility.id = :utilityId " +
        "AND (:startDate IS NULL OR b.startTime >= :startDate) " +
        "AND (:endDate IS NULL OR b.endTime <= :endDate)"
    )
    Page<UtilityBookings> findByUtilityIdAndDateRange(
        @Param("utilityId") Long utilityId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate,
        Pageable pageable
    );

    @Query("select ub from UtilityBookings ub where ub.user.id = :userId")
    Page<UtilityBookings> findByUserId(@Param("userId") Long userId, Pageable pageable);
}
