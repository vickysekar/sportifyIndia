package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.OneTimeEventSubscribers;
import com.sportifyindia.app.repository.OneTimeEventSubscribersRepository;
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
 * Spring Data Elasticsearch repository for the {@link OneTimeEventSubscribers} entity.
 */
public interface OneTimeEventSubscribersSearchRepository
    extends ElasticsearchRepository<OneTimeEventSubscribers, Long>, OneTimeEventSubscribersSearchRepositoryInternal {}

interface OneTimeEventSubscribersSearchRepositoryInternal {
    Page<OneTimeEventSubscribers> search(String query, Pageable pageable);

    Page<OneTimeEventSubscribers> search(Query query);

    @Async
    void index(OneTimeEventSubscribers entity);

    @Async
    void deleteFromIndexById(Long id);
}

class OneTimeEventSubscribersSearchRepositoryInternalImpl implements OneTimeEventSubscribersSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final OneTimeEventSubscribersRepository repository;

    OneTimeEventSubscribersSearchRepositoryInternalImpl(
        ElasticsearchTemplate elasticsearchTemplate,
        OneTimeEventSubscribersRepository repository
    ) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<OneTimeEventSubscribers> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<OneTimeEventSubscribers> search(Query query) {
        SearchHits<OneTimeEventSubscribers> searchHits = elasticsearchTemplate.search(query, OneTimeEventSubscribers.class);
        List<OneTimeEventSubscribers> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(OneTimeEventSubscribers entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), OneTimeEventSubscribers.class);
    }
}
