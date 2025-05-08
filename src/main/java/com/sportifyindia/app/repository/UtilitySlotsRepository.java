package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.UtilitySlots;
import com.sportifyindia.app.domain.enumeration.UtilitySlotStatusEnum;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the UtilitySlots entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UtilitySlotsRepository extends JpaRepository<UtilitySlots, Long> {
    @Query("SELECT s FROM UtilitySlots s WHERE s.utility.id = :utilityId AND s.date = :date AND s.status = :status")
    Page<UtilitySlots> findByUtilityIdAndDateAndStatus(
        @Param("utilityId") Long utilityId,
        @Param("date") Instant date,
        @Param("status") UtilitySlotStatusEnum status,
        Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM UtilitySlots s WHERE s.id = :id")
    Optional<UtilitySlots> findByIdWithLock(@Param("id") Long id);

    @Query("SELECT s FROM UtilitySlots s WHERE s.utility.id = :utilityId AND s.date BETWEEN :startDate AND :endDate")
    List<UtilitySlots> findByUtilityIdAndDateRange(
        @Param("utilityId") Long utilityId,
        @Param("startDate") Instant startDate,
        @Param("endDate") Instant endDate
    );
}
