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

    /**
     * Find all subscription plans for a specific course.
     *
     * @param courseId the ID of the course
     * @param pageable the pagination information
     * @return the list of subscription plans for the course
     */
    Page<SubscriptionPlan> findByCourseId(Long courseId, Pageable pageable);

    /**
     * Find all subscription plans for a specific course with eager loaded relationships.
     *
     * @param courseId the ID of the course
     * @param pageable the pagination information
     * @return the list of subscription plans for the course with eager loaded relationships
     */
    @EntityGraph(attributePaths = { "subscriptionAvailableDays", "subscriptionAvailableDays.timeSlots" })
    Page<SubscriptionPlan> findByCourseIdWithEagerRelationships(Long courseId, Pageable pageable);
}
