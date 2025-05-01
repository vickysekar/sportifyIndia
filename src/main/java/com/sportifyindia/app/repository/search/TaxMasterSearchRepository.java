package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.TaxMaster;
import com.sportifyindia.app.repository.TaxMasterRepository;
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
 * Spring Data Elasticsearch repository for the {@link TaxMaster} entity.
 */
public interface TaxMasterSearchRepository extends ElasticsearchRepository<TaxMaster, Long>, TaxMasterSearchRepositoryInternal {}

interface TaxMasterSearchRepositoryInternal {
    Page<TaxMaster> search(String query, Pageable pageable);

    Page<TaxMaster> search(Query query);

    @Async
    void index(TaxMaster entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TaxMasterSearchRepositoryInternalImpl implements TaxMasterSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TaxMasterRepository repository;

    TaxMasterSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TaxMasterRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<TaxMaster> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<TaxMaster> search(Query query) {
        SearchHits<TaxMaster> searchHits = elasticsearchTemplate.search(query, TaxMaster.class);
        List<TaxMaster> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(TaxMaster entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), TaxMaster.class);
    }
}
