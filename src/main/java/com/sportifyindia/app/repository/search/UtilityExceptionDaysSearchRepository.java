package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.UtilityExceptionDays;
import com.sportifyindia.app.repository.UtilityExceptionDaysRepository;
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
 * Spring Data Elasticsearch repository for the {@link UtilityExceptionDays} entity.
 */
public interface UtilityExceptionDaysSearchRepository
    extends ElasticsearchRepository<UtilityExceptionDays, Long>, UtilityExceptionDaysSearchRepositoryInternal {}

interface UtilityExceptionDaysSearchRepositoryInternal {
    Page<UtilityExceptionDays> search(String query, Pageable pageable);

    Page<UtilityExceptionDays> search(Query query);

    @Async
    void index(UtilityExceptionDays entity);

    @Async
    void deleteFromIndexById(Long id);
}

class UtilityExceptionDaysSearchRepositoryInternalImpl implements UtilityExceptionDaysSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UtilityExceptionDaysRepository repository;

    UtilityExceptionDaysSearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        UtilityExceptionDaysRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UtilityExceptionDays> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<UtilityExceptionDays> search(Query query) {
        SearchHits<UtilityExceptionDays> searchHits = elasticsearchTemplate.search(query, UtilityExceptionDays.class);
        List<UtilityExceptionDays> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UtilityExceptionDays entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), UtilityExceptionDays.class);
    }
}
