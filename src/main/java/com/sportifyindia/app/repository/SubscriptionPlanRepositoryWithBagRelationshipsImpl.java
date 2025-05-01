package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.SubscriptionPlan;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

/**
 * Utility repository to load bag relationships based on https://vladmihalcea.com/hibernate-multiplebagfetchexception/
 */
public class SubscriptionPlanRepositoryWithBagRelationshipsImpl implements SubscriptionPlanRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<SubscriptionPlan> fetchBagRelationships(Optional<SubscriptionPlan> subscriptionPlan) {
        return subscriptionPlan.map(this::fetchSubscriptionAvailableDays);
    }

    @Override
    public Page<SubscriptionPlan> fetchBagRelationships(Page<SubscriptionPlan> subscriptionPlans) {
        return new PageImpl<>(
            fetchBagRelationships(subscriptionPlans.getContent()),
            subscriptionPlans.getPageable(),
            subscriptionPlans.getTotalElements()
        );
    }

    @Override
    public List<SubscriptionPlan> fetchBagRelationships(List<SubscriptionPlan> subscriptionPlans) {
        return Optional.of(subscriptionPlans).map(this::fetchSubscriptionAvailableDays).orElse(Collections.emptyList());
    }

    SubscriptionPlan fetchSubscriptionAvailableDays(SubscriptionPlan result) {
        return entityManager
            .createQuery(
                "select subscriptionPlan from SubscriptionPlan subscriptionPlan left join fetch subscriptionPlan.subscriptionAvailableDays where subscriptionPlan.id = :id",
                SubscriptionPlan.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<SubscriptionPlan> fetchSubscriptionAvailableDays(List<SubscriptionPlan> subscriptionPlans) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, subscriptionPlans.size()).forEach(index -> order.put(subscriptionPlans.get(index).getId(), index));
        List<SubscriptionPlan> result = entityManager
            .createQuery(
                "select subscriptionPlan from SubscriptionPlan subscriptionPlan left join fetch subscriptionPlan.subscriptionAvailableDays where subscriptionPlan in :subscriptionPlans",
                SubscriptionPlan.class
            )
            .setParameter("subscriptionPlans", subscriptionPlans)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
