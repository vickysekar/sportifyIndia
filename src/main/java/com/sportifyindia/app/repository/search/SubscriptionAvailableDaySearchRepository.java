package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.SubscriptionAvailableDay;
import com.sportifyindia.app.repository.SubscriptionAvailableDayRepository;
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
 * Spring Data Elasticsearch repository for the {@link SubscriptionAvailableDay} entity.
 */
public interface SubscriptionAvailableDaySearchRepository
    extends ElasticsearchRepository<SubscriptionAvailableDay, Long>, SubscriptionAvailableDaySearchRepositoryInternal {}

interface SubscriptionAvailableDaySearchRepositoryInternal {
    Page<SubscriptionAvailableDay> search(String query, Pageable pageable);

    Page<SubscriptionAvailableDay> search(Query query);

    @Async
    void index(SubscriptionAvailableDay entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SubscriptionAvailableDaySearchRepositoryInternalImpl implements SubscriptionAvailableDaySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SubscriptionAvailableDayRepository repository;

    SubscriptionAvailableDaySearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        SubscriptionAvailableDayRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<SubscriptionAvailableDay> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<SubscriptionAvailableDay> search(Query query) {
        SearchHits<SubscriptionAvailableDay> searchHits = elasticsearchTemplate.search(query, SubscriptionAvailableDay.class);
        List<SubscriptionAvailableDay> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(SubscriptionAvailableDay entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SubscriptionAvailableDay.class);
    }
}
