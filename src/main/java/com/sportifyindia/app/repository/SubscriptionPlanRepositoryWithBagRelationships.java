package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.SubscriptionPlan;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface SubscriptionPlanRepositoryWithBagRelationships {
    Optional<SubscriptionPlan> fetchBagRelationships(Optional<SubscriptionPlan> subscriptionPlan);

    List<SubscriptionPlan> fetchBagRelationships(List<SubscriptionPlan> subscriptionPlans);

    Page<SubscriptionPlan> fetchBagRelationships(Page<SubscriptionPlan> subscriptionPlans);
}
