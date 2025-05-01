package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.UtilityAvailableDays;
import com.sportifyindia.app.repository.UtilityAvailableDaysRepository;
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
 * Spring Data Elasticsearch repository for the {@link UtilityAvailableDays} entity.
 */
public interface UtilityAvailableDaysSearchRepository
    extends ElasticsearchRepository<UtilityAvailableDays, Long>, UtilityAvailableDaysSearchRepositoryInternal {}

interface UtilityAvailableDaysSearchRepositoryInternal {
    Page<UtilityAvailableDays> search(String query, Pageable pageable);

    Page<UtilityAvailableDays> search(Query query);

    @Async
    void index(UtilityAvailableDays entity);

    @Async
    void deleteFromIndexById(Long id);
}

class UtilityAvailableDaysSearchRepositoryInternalImpl implements UtilityAvailableDaysSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final UtilityAvailableDaysRepository repository;

    UtilityAvailableDaysSearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        UtilityAvailableDaysRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<UtilityAvailableDays> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<UtilityAvailableDays> search(Query query) {
        SearchHits<UtilityAvailableDays> searchHits = elasticsearchTemplate.search(query, UtilityAvailableDays.class);
        List<UtilityAvailableDays> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(UtilityAvailableDays entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), UtilityAvailableDays.class);
    }
}
