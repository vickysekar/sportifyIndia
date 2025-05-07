package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.Utility;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Utility entity.
 *
 * When extending this class, extend UtilityRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface UtilityRepository extends UtilityRepositoryWithBagRelationships, JpaRepository<Utility, Long> {
    default Optional<Utility> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<Utility> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<Utility> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }

    @Query("select utility from Utility utility where utility.facility.id = :facilityId")
    Page<Utility> findByFacilityId(@Param("facilityId") Long facilityId, Pageable pageable);
}
