package com.sportifyindia.app.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import com.sportifyindia.app.domain.Notes;
import com.sportifyindia.app.repository.NotesRepository;
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
 * Spring Data Elasticsearch repository for the {@link Notes} entity.
 */
public interface NotesSearchRepository extends ElasticsearchRepository<Notes, Long>, NotesSearchRepositoryInternal {}

interface NotesSearchRepositoryInternal {
    Page<Notes> search(String query, Pageable pageable);

    Page<Notes> search(Query query);

    @Async
    void index(Notes entity);

    @Async
    void deleteFromIndexById(Long id);
}

class NotesSearchRepositoryInternalImpl implements NotesSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final NotesRepository repository;

    NotesSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, NotesRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Notes> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Notes> search(Query query) {
        SearchHits<Notes> searchHits = elasticsearchTemplate.search(query, Notes.class);
        List<Notes> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Notes entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Notes.class);
    }
}
