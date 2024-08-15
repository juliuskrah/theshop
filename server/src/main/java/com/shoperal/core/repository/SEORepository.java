package com.shoperal.core.repository;

import java.util.UUID;

import com.shoperal.core.model.Product;
import com.shoperal.core.model.SEO;

import org.springframework.data.repository.CrudRepository;

/**
 * @author Julius Krah
 */
public interface SEORepository extends CrudRepository<SEO, UUID> {
    Iterable<SEO> findByProduct(Product product);
}
