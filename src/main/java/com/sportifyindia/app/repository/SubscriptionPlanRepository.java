package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.SubscriptionPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the SubscriptionPlan entity.
 *
 * When extending this class, extend SubscriptionPlanRepositoryWithBagRelationships too.
 * For more information refer to https://github.com/jhipster/generator-jhipster/issues/17990.
 */
@Repository
public interface SubscriptionPlanRepository extends SubscriptionPlanRepositoryWithBagRelationships, JpaRepository<SubscriptionPlan, Long> {
    default Optional<SubscriptionPlan> findOneWithEagerRelationships(Long id) {
        return this.fetchBagRelationships(this.findById(id));
    }

    default List<SubscriptionPlan> findAllWithEagerRelationships() {
        return this.fetchBagRelationships(this.findAll());
    }

    default Page<SubscriptionPlan> findAllWithEagerRelationships(Pageable pageable) {
        return this.fetchBagRelationships(this.findAll(pageable));
    }
}
