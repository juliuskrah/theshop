package com.shoperal.core.repository;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.lang.Nullable;

/**
 * @author Julius Krah
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends CrudRepository<T, ID> {
    List<T> findAll(Specification<T> spec, int maxResults, Sort sort);
    List<T> findAll(Specification<T> spec, int maxResults);
    List<T> findAll(int maxResults, Sort sort);
    List<T> findAll(int maxResults);
    <P> List<P> findAll(@Nullable Specification<P> spec, int maxResults, Sort sort, Class<P> projection);
    <P> List<P> findAll(@Nullable Specification<P> spec, int maxResults, Class<P> projection);
}
