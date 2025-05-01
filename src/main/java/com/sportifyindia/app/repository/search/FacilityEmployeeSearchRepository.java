package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.FacilityEmployee;
import com.sportifyindia.app.repository.FacilityEmployeeRepository;
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
 * Spring Data Elasticsearch repository for the {@link FacilityEmployee} entity.
 */
public interface FacilityEmployeeSearchRepository
    extends ElasticsearchRepository<FacilityEmployee, Long>, FacilityEmployeeSearchRepositoryInternal {}

interface FacilityEmployeeSearchRepositoryInternal {
    Page<FacilityEmployee> search(String query, Pageable pageable);

    Page<FacilityEmployee> search(Query query);

    @Async
    void index(FacilityEmployee entity);

    @Async
    void deleteFromIndexById(Long id);
}

class FacilityEmployeeSearchRepositoryInternalImpl implements FacilityEmployeeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final FacilityEmployeeRepository repository;

    FacilityEmployeeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, FacilityEmployeeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<FacilityEmployee> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<FacilityEmployee> search(Query query) {
        SearchHits<FacilityEmployee> searchHits = elasticsearchTemplate.search(query, FacilityEmployee.class);
        List<FacilityEmployee> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(FacilityEmployee entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), FacilityEmployee.class);
    }
}
