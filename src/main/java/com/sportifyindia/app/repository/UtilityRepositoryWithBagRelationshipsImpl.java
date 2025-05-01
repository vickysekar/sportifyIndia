package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.Utility;
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
public class UtilityRepositoryWithBagRelationshipsImpl implements UtilityRepositoryWithBagRelationships {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Utility> fetchBagRelationships(Optional<Utility> utility) {
        return utility.map(this::fetchUtilityAvailableDays);
    }

    @Override
    public Page<Utility> fetchBagRelationships(Page<Utility> utilities) {
        return new PageImpl<>(fetchBagRelationships(utilities.getContent()), utilities.getPageable(), utilities.getTotalElements());
    }

    @Override
    public List<Utility> fetchBagRelationships(List<Utility> utilities) {
        return Optional.of(utilities).map(this::fetchUtilityAvailableDays).orElse(Collections.emptyList());
    }

    Utility fetchUtilityAvailableDays(Utility result) {
        return entityManager
            .createQuery(
                "select utility from Utility utility left join fetch utility.utilityAvailableDays where utility.id = :id",
                Utility.class
            )
            .setParameter("id", result.getId())
            .getSingleResult();
    }

    List<Utility> fetchUtilityAvailableDays(List<Utility> utilities) {
        HashMap<Object, Integer> order = new HashMap<>();
        IntStream.range(0, utilities.size()).forEach(index -> order.put(utilities.get(index).getId(), index));
        List<Utility> result = entityManager
            .createQuery(
                "select utility from Utility utility left join fetch utility.utilityAvailableDays where utility in :utilities",
                Utility.class
            )
            .setParameter("utilities", utilities)
            .getResultList();
        Collections.sort(result, (o1, o2) -> Integer.compare(order.get(o1.getId()), order.get(o2.getId())));
        return result;
    }
}
