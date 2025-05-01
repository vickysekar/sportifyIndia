package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.OneTimeEvent;
import com.sportifyindia.app.repository.OneTimeEventRepository;
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
 * Spring Data Elasticsearch repository for the {@link OneTimeEvent} entity.
 */
public interface OneTimeEventSearchRepository extends ElasticsearchRepository<OneTimeEvent, Long>, OneTimeEventSearchRepositoryInternal {}

interface OneTimeEventSearchRepositoryInternal {
    Page<OneTimeEvent> search(String query, Pageable pageable);

    Page<OneTimeEvent> search(Query query);

    @Async
    void index(OneTimeEvent entity);

    @Async
    void deleteFromIndexById(Long id);
}

class OneTimeEventSearchRepositoryInternalImpl implements OneTimeEventSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OneTimeEventRepository repository;

    OneTimeEventSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, OneTimeEventRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<OneTimeEvent> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<OneTimeEvent> search(Query query) {
        SearchHits<OneTimeEvent> searchHits = elasticsearchTemplate.search(query, OneTimeEvent.class);
        List<OneTimeEvent> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(OneTimeEvent entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), OneTimeEvent.class);
    }
}
