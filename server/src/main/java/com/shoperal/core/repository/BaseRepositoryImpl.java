package com.shoperal.core.repository;

import static com.shoperal.core.repository.QueryUtilsWrapper.toExpressionRecursively;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.projection.CollectionAwareProjectionFactory;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.projection.ProjectionFactory;

/**
 * Custom class that supports adding maxResults without the need for Pageable
 * 
 * @author Julius Krah
 */
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private ProjectionFactory projectionFactory = new CollectionAwareProjectionFactory();
    private final EntityManager em;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
    }

    @Override
    public List<T> findAll(Specification<T> spec, int maxResults, Sort sort) {
        TypedQuery<T> query = getQuery(spec, sort);

        if (maxResults < 1) {
            throw new IllegalArgumentException("Max results must not be less than one!");
        }
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

    @Override
    public List<T> findAll(Specification<T> spec, int maxResults) {
        return findAll(spec, maxResults, Sort.unsorted());
    }

    @Override
    public List<T> findAll(int maxResults, Sort sort) {
        return findAll(null, maxResults, sort);
    }

    @Override
    public List<T> findAll(int maxResults) {
        return findAll(maxResults, Sort.unsorted());
    }

    /**
     * Implementation borrowed from https://gist.github.com/koraktor/52d5f1ffcc12768000e5a4e7b9fa0d1f
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public <P> List<P> findAll(Specification<P> spec, int maxResults, Sort sort, Class<P> projection) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> tupleQuery = criteriaBuilder.createTupleQuery();
        Root<?> root = tupleQuery.from(getDomainClass());
        // Gathering selections from the projection class
        Set<Selection<?>> selections = new HashSet<>();
        List<PropertyDescriptor> inputProperties = projectionFactory.getProjectionInformation(projection)
                .getInputProperties();
        for (PropertyDescriptor propertyDescriptor : inputProperties) {
            String property = propertyDescriptor.getName();
            PropertyPath path = PropertyPath.from(property, getDomainClass());
            selections.add(toExpressionRecursively(root, path).alias(property));
        }
        // CriteriaBuilder builder = em.getCriteriaBuilder();
        if(spec != null) {
            Predicate predicate = spec.toPredicate((Root) root, tupleQuery, criteriaBuilder);
            if (predicate != null) {
                // restrict
                tupleQuery.where(predicate);
		    }
        }
        // Select and order
        tupleQuery.multiselect(new ArrayList<>(selections)) //
                .orderBy(QueryUtils.toOrders(sort, root, criteriaBuilder));

        TypedQuery<Tuple> query = em.createQuery(tupleQuery);
        query.setMaxResults(maxResults);
        List<Tuple> results = query.getResultList();
        // Create maps for each result tuple
        List<P> projectedResults = new ArrayList<>(results.size());
        for (Tuple tuple : results) {
            Map<String, Object> mappedResult = new HashMap<>(tuple.getElements().size());
            for (TupleElement<?> element : tuple.getElements()) {
                String name = element.getAlias();
                mappedResult.put(name, tuple.get(name));
            }

            projectedResults.add(projectionFactory.createProjection(projection, mappedResult));
        }
        return projectedResults;
    }

    @Override
    public <P> List<P> findAll(Specification<P> spec, int maxResults, Class<P> projection) {
        return findAll(spec, maxResults, Sort.unsorted(), projection);
    }

}
