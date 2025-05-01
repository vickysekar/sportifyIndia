package com.sportifyindia.app.repository;

import com.sportifyindia.app.domain.Utility;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;

public interface UtilityRepositoryWithBagRelationships {
    Optional<Utility> fetchBagRelationships(Optional<Utility> utility);

    List<Utility> fetchBagRelationships(List<Utility> utilities);

    Page<Utility> fetchBagRelationships(Page<Utility> utilities);
}
