package com.shoperal.core.business;

import java.util.Map;
import java.util.UUID;

import jakarta.validation.Valid;

import com.shoperal.core.dto.ProductDTO;

import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;

/**
 * The service class contains methods that handle the business operations and
 * rules for Shoperal Product.
 * 
 * @implNote As a rule, the following recommendations should be adopted
 *           <ol>
 *           <li>Read operations should be prefixed with <code>find*</code></li>
 *           <li>Write operations should be prefixed with
 *           <code>create*</code></li>
 *           <li>Update operations should be prefixed with
 *           <code>modify*</code></li>
 *           <li>Delete operations should be prefixed with
 *           <code>remove*</code></li>
 *           </ol>
 *           This should make it easy for AOP <code>pointcut expressions</code>
 *           to match
 * @author Julius Krah
 */
@Validated
public interface ProductService {

    /**
     * List products.
     * 
     * @return products
     */
    Iterable<ProductDTO> findProducts();

    /**
     * List products using pagination strategy. Products are listed using the provided query parameters
     * 
     * @param options the criteria for pagination
     * @param maxResults the maximum results to return
     * @param sort the fields to sort by
     * @return products
     */
    PaginationWrapper<ProductDTO> findProducts(Map<String, String> options, int maxResults, Sort sort);

    /**
     * Creates a new product
     * 
     * @param product new product
     * @return product
     */
    ProductDTO createProduct(@Valid ProductDTO product);

    /**
     * Update an existing product
     * 
     * @param id
     * @param product
     * @return product
     */
    ProductDTO modifyProduct(UUID id, @Valid ProductDTO product);

    /**
     * Delte a product. This operation is idempotent
     * 
     * @param id
     */
    void removeProduct(UUID id);
}
