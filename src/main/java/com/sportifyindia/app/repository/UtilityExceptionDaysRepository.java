package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.UtilityExceptionDays;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UtilityExceptionDays entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UtilityExceptionDaysRepository extends JpaRepository<UtilityExceptionDays, Long> {
    @Query(
        "select exceptionDay from UtilityExceptionDays exceptionDay where exceptionDay.utility.id = :utilityId and exceptionDay.date between :fromDate and :toDate"
    )
    Page<UtilityExceptionDays> findByUtilityIdAndDateBetween(
        @Param("utilityId") Long utilityId,
        @Param("fromDate") Instant fromDate,
        @Param("toDate") Instant toDate,
        Pageable pageable
    );
}
