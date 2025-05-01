package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.Charge;
import com.sportifyindia.app.repository.ChargeRepository;
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
 * Spring Data Elasticsearch repository for the {@link Charge} entity.
 */
public interface ChargeSearchRepository extends ElasticsearchRepository<Charge, Long>, ChargeSearchRepositoryInternal {}

interface ChargeSearchRepositoryInternal {
    Page<Charge> search(String query, Pageable pageable);

    Page<Charge> search(Query query);

    @Async
    void index(Charge entity);

    @Async
    void deleteFromIndexById(Long id);
}

class ChargeSearchRepositoryInternalImpl implements ChargeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final ChargeRepository repository;

    ChargeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, ChargeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Charge> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Charge> search(Query query) {
        SearchHits<Charge> searchHits = elasticsearchTemplate.search(query, Charge.class);
        List<Charge> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Charge entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Charge.class);
    }
}
