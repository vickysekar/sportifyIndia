package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.Address;
import com.sportifyindia.app.repository.AddressRepository;
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
 * Spring Data Elasticsearch repository for the {@link Address} entity.
 */
public interface AddressSearchRepository extends ElasticsearchRepository<Address, Long>, AddressSearchRepositoryInternal {}

interface AddressSearchRepositoryInternal {
    Page<Address> search(String query, Pageable pageable);

    Page<Address> search(Query query);

    @Async
    void index(Address entity);

    @Async
    void deleteFromIndexById(Long id);
}

class AddressSearchRepositoryInternalImpl implements AddressSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final AddressRepository repository;

    AddressSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, AddressRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Address> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Address> search(Query query) {
        SearchHits<Address> searchHits = elasticsearchTemplate.search(query, Address.class);
        List<Address> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Address entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Address.class);
    }
}
