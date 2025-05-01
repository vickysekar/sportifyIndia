package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.UtilityBookings;
import com.sportifyindia.app.repository.UtilityBookingsRepository;
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
 * Spring Data Elasticsearch repository for the {@link UtilityBookings} entity.
 */
public interface UtilityBookingsSearchRepository
    extends ElasticsearchRepository<UtilityBookings, Long>, UtilityBookingsSearchRepositoryInternal {}

interface UtilityBookingsSearchRepositoryInternal {
    Page<UtilityBookings> search(String query, Pageable pageable);

    Page<UtilityBookings> search(Query query);

    @Async
    void index(UtilityBookings entity);

    @Async
    void deleteFromIndexById(Long id);
}

class UtilityBookingsSearchRepositoryInternalImpl implements UtilityBookingsSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UtilityBookingsRepository repository;

    UtilityBookingsSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, UtilityBookingsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UtilityBookings> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<UtilityBookings> search(Query query) {
        SearchHits<UtilityBookings> searchHits = elasticsearchTemplate.search(query, UtilityBookings.class);
        List<UtilityBookings> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UtilityBookings entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), UtilityBookings.class);
    }
}
