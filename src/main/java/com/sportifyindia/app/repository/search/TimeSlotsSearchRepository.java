package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.TimeSlots;
import com.sportifyindia.app.repository.TimeSlotsRepository;
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
 * Spring Data Elasticsearch repository for the {@link TimeSlots} entity.
 */
public interface TimeSlotsSearchRepository extends ElasticsearchRepository<TimeSlots, Long>, TimeSlotsSearchRepositoryInternal {}

interface TimeSlotsSearchRepositoryInternal {
    Page<TimeSlots> search(String query, Pageable pageable);

    Page<TimeSlots> search(Query query);

    @Async
    void index(TimeSlots entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TimeSlotsSearchRepositoryInternalImpl implements TimeSlotsSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TimeSlotsRepository repository;

    TimeSlotsSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TimeSlotsRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TimeSlots> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<TimeSlots> search(Query query) {
        SearchHits<TimeSlots> searchHits = elasticsearchTemplate.search(query, TimeSlots.class);
        List<TimeSlots> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TimeSlots entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), TimeSlots.class);
    }
}
