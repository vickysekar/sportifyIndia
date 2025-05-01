package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.Tax;
import com.sportifyindia.app.repository.TaxRepository;
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
 * Spring Data Elasticsearch repository for the {@link Tax} entity.
 */
public interface TaxSearchRepository extends ElasticsearchRepository<Tax, Long>, TaxSearchRepositoryInternal {}

interface TaxSearchRepositoryInternal {
    Page<Tax> search(String query, Pageable pageable);

    Page<Tax> search(Query query);

    @Async
    void index(Tax entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TaxSearchRepositoryInternalImpl implements TaxSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TaxRepository repository;

    TaxSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TaxRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Tax> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Tax> search(Query query) {
        SearchHits<Tax> searchHits = elasticsearchTemplate.search(query, Tax.class);
        List<Tax> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Tax entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Tax.class);
    }
}
