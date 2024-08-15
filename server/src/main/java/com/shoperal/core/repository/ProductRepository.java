package com.shoperal.core.repository;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.shoperal.core.model.Product;
import com.shoperal.core.model.ProductStatus;
import com.shoperal.core.projection.AdminProduct;

/**
 * @author Julius Krah
 */
public interface ProductRepository extends BaseRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
    Iterable<Product> findByStatus(ProductStatus status);

    Iterable<Product> findByNameContains(String name);

    @Query("SELECT p FROM #{#entityName} p WHERE p.description LIKE %?#{escape([0])}% escape ?#{escapeCharacter()}")
    Iterable<Product> findByDescription(String description);

    @Query("SELECT p FROM ProductCategory pc JOIN pc.category c " //
        + "JOIN pc.product p WHERE c.id = :categoryId")
    Iterable<Product> findByCategory(UUID categoryId);

    @EntityGraph(attributePaths = "seo")
    @Override
    Optional<Product> findById(UUID id);

    @EntityGraph(attributePaths = "seo")
    Optional<Product> findByFriendlyUriFragment(URI handle);

    @Query(value = "WITH products AS(" //
            + "     SELECT p.id, p.name, p.featured_media, p.vendor, p.type, p.status FROM product p" //
            + "     WHERE p.name < :name OR name = :name AND id < :id" //
            + "     ORDER BY name DESC, id DESC LIMIT :size" //
            + ") SELECT CAST(p.id as VARCHAR) AS id, p.name AS name, p.featured_media AS featuredMedia, p.vendor AS vendor, p.type AS type, p.status AS status " //
            + "FROM products p ORDER BY name ASC, id ASC", //
            nativeQuery = true)
    Iterable<AdminProduct> findByNameLessThanAsc(String name, UUID id, int size);

    @Query(value = "WITH products AS(" //
            + "     SELECT p.id, p.name, p.featured_media, p.vendor, p.type, p.status FROM product p" //
            + "     WHERE p.name > :name OR name = :name AND id > :id" //
            + "     ORDER BY name ASC, id ASC LIMIT :size" //
            + ") SELECT CAST(p.id as VARCHAR) AS id, p.name AS name, p.featured_media AS featuredMedia, p.vendor AS vendor, p.type AS type, p.status AS status " //
            + "FROM products p ORDER BY name DESC, id DESC", //
            nativeQuery = true)
    Iterable<AdminProduct> findByNameGreaterThanDesc(String name, UUID id, int size);

    <T> Iterable<T> findAllBy(Class<T> type);

    <T> Iterable<T> findByStatus(ProductStatus status, Class<T> type);

    <T> Iterable<T> findByNameContains(String name, Class<T> type);

    <T> Iterable<T> findByDescriptionContains(String description, Class<T> type);

    @Query("SELECT COUNT(p) FROM ProductCategory pc JOIN pc.category c " //
        + "JOIN pc.product p WHERE c.id = :categoryId")
    long countByCategory(UUID categoryId);
}
