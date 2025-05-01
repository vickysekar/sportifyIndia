package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.LeadActivity;
import com.sportifyindia.app.repository.LeadActivityRepository;
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
 * Spring Data Elasticsearch repository for the {@link LeadActivity} entity.
 */
public interface LeadActivitySearchRepository extends ElasticsearchRepository<LeadActivity, Long>, LeadActivitySearchRepositoryInternal {}

interface LeadActivitySearchRepositoryInternal {
    Page<LeadActivity> search(String query, Pageable pageable);

    Page<LeadActivity> search(Query query);

    @Async
    void index(LeadActivity entity);

    @Async
    void deleteFromIndexById(Long id);
}

class LeadActivitySearchRepositoryInternalImpl implements LeadActivitySearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final LeadActivityRepository repository;

    LeadActivitySearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, LeadActivityRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<LeadActivity> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<LeadActivity> search(Query query) {
        SearchHits<LeadActivity> searchHits = elasticsearchTemplate.search(query, LeadActivity.class);
        List<LeadActivity> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(LeadActivity entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), LeadActivity.class);
    }
}
