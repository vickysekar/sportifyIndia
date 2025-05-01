package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.UtilitySlots;
import com.sportifyindia.app.repository.UtilitySlotsRepository;
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
 * Spring Data Elasticsearch repository for the {@link UtilitySlots} entity.
 */
public interface UtilitySlotsSearchRepository extends ElasticsearchRepository<UtilitySlots, Long>, UtilitySlotsSearchRepositoryInternal {}

interface UtilitySlotsSearchRepositoryInternal {
    Page<UtilitySlots> search(String query, Pageable pageable);

    Page<UtilitySlots> search(Query query);

    @Async
    void index(UtilitySlots entity);

    @Async
    void deleteFromIndexById(Long id);
}

class UtilitySlotsSearchRepositoryInternalImpl implements UtilitySlotsSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UtilitySlotsRepository repository;

    UtilitySlotsSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UtilitySlotsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UtilitySlots> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<UtilitySlots> search(Query query) {
        SearchHits<UtilitySlots> searchHits = elasticsearchTemplate.search(query, UtilitySlots.class);
        List<UtilitySlots> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UtilitySlots entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), UtilitySlots.class);
    }
}
