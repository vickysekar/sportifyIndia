package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.SubscriptionPlan;
import com.sportifyindia.app.repository.SubscriptionPlanRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link SubscriptionPlan} entity.
 */
public interface SubscriptionPlanSearchRepository
    extends ElasticsearchRepository<SubscriptionPlan, Long>, SubscriptionPlanSearchRepositoryInternal {}

interface SubscriptionPlanSearchRepositoryInternal {
    Page<SubscriptionPlan> search(String query, Pageable pageable);

    Page<SubscriptionPlan> search(Query query);

    @Async
    void index(SubscriptionPlan entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SubscriptionPlanSearchRepositoryInternalImpl implements SubscriptionPlanSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SubscriptionPlanRepository repository;

    SubscriptionPlanSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SubscriptionPlanRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<SubscriptionPlan> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<SubscriptionPlan> search(Query query) {
        SearchHits<SubscriptionPlan> searchHits = elasticsearchTemplate.search(query, SubscriptionPlan.class);
        List<SubscriptionPlan> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(SubscriptionPlan entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SubscriptionPlan.class);
    }
}
