package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.SaleLead;
import com.sportifyindia.app.repository.SaleLeadRepository;
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
 * Spring Data Elasticsearch repository for the {@link SaleLead} entity.
 */
public interface SaleLeadSearchRepository extends ElasticsearchRepository<SaleLead, Long>, SaleLeadSearchRepositoryInternal {}

interface SaleLeadSearchRepositoryInternal {
    Page<SaleLead> search(String query, Pageable pageable);

    Page<SaleLead> search(Query query);

    @Async
    void index(SaleLead entity);

    @Async
    void deleteFromIndexById(Long id);
}

class SaleLeadSearchRepositoryInternalImpl implements SaleLeadSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final SaleLeadRepository repository;

    SaleLeadSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, SaleLeadRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<SaleLead> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<SaleLead> search(Query query) {
        SearchHits<SaleLead> searchHits = elasticsearchTemplate.search(query, SaleLead.class);
        List<SaleLead> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(SaleLead entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), SaleLead.class);
    }
}
