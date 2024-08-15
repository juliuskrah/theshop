package com.shoperal.core.utility;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;

import com.shoperal.core.model.Category;
import com.shoperal.core.model.Category_;
import com.shoperal.core.model.Product;
import com.shoperal.core.model.ProductCategory;
import com.shoperal.core.model.Product_;
import com.shoperal.core.model.ProductCategory_;
import com.shoperal.core.projection.AdminProduct;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.Assert;

public class Specifications {
    private Specifications() {
    }

    /**
     * SELECT p FROM product p WHERE (p.name > :name) OR (p.name = :name AND p.id >
     * :id)
     * 
     * @apiNote Product name A-Z
     */
    public static Specification<Product> productNameGreaterThan(Map<String, String> predicates) {
        Assert.isTrue(predicates.containsKey("name"), "'name' must be present");
        Assert.isTrue(predicates.containsKey("id"), "'id' must be present");
        return (root, query, builder) -> {
            // p.name > :name
            Predicate nameGreaterThan = builder.greaterThan(root.get(Product_.name), predicates.get("name"));
            // p.name = :name
            Predicate nameEqual = builder.equal(root.get(Product_.name), predicates.get("name"));

            // p.id > :id
            Predicate idGreaterThan = builder.greaterThan(root.get( //
                    Product_.id), UUID.fromString(predicates.get("id")));

            // p.name = :name AND p.id > :id
            Predicate and = builder.and(nameEqual, idGreaterThan);
            // (p.name > :name) OR (p.name = :name AND p.id > :id)
            return builder.or(nameGreaterThan, and);
        };
    }

    /**
     * SELECT p.name, p.featured_media, p.vendor, p.id, p.type, p.status FROM
     * product p WHERE (p.name > :name) OR (p.name = :name AND p.id > :id)
     * 
     * @apiNote Product name A-Z
     */
    public static <T extends AdminProduct> Specification<T> projectProductNameGreaterThan(
            Map<String, String> predicates) {
        Assert.isTrue(predicates.containsKey("name"), "'name' must be present");
        Assert.isTrue(predicates.containsKey("id"), "'id' must be present");
        return (root, query, builder) -> {
            // p.name > :name
            Predicate nameGreaterThan = builder.greaterThan(root.get("name"), predicates.get("name"));
            // p.name = :name
            Predicate nameEqual = builder.equal(root.get("name"), predicates.get("name"));

            // p.id > :id
            Predicate idGreaterThan = builder.greaterThan(root.get("id"), UUID.fromString(predicates.get("id")));

            // p.name = :name AND p.id > :id
            Predicate and = builder.and(nameEqual, idGreaterThan);
            // (p.name > :name) OR (p.name = :name AND p.id > :id)
            return builder.or(nameGreaterThan, and);
        };
    }

    /**
     * SELECT p FROM product p WHERE (p.name < :name) OR (p.name = :name AND p.id <
     * :id)
     * 
     * @apiNote Product name Z-A
     */
    public static Specification<Product> productNameLessThan(Map<String, String> predicates) {
        Assert.isTrue(predicates.containsKey("name"), "'name' must be present");
        Assert.isTrue(predicates.containsKey("id"), "'id' must be present");
        return (root, query, builder) -> {
            // p.name < :name
            Predicate namelessThan = builder.lessThan(root.get(Product_.name), predicates.get("name"));
            // p.name = :name
            Predicate nameEqual = builder.equal(root.get(Product_.name), predicates.get("name"));

            // p.id < :id
            Predicate idLessThan = builder.lessThan(root.get( //
                    Product_.id), UUID.fromString(predicates.get("id")));

            // p.name = :name AND p.id > :id
            Predicate and = builder.and(nameEqual, idLessThan);
            // (p.name > :name) OR (p.name = :name AND p.id > :id)
            return builder.or(namelessThan, and);
        };
    }

    /**
     * SELECT p.name, p.featured_media, p.vendor, p.id, p.type, p.status FROM
     * product p WHERE (p.name < :name) OR (p.name = :name AND p.id < :id)
     * 
     * @apiNote Product name Z-A
     */
    public static <T extends AdminProduct> Specification<T> projectProductNameLessThan(Map<String, String> predicates) {
        Assert.isTrue(predicates.containsKey("name"), "'name' must be present");
        Assert.isTrue(predicates.containsKey("id"), "'id' must be present");
        return (root, query, builder) -> {
            // p.name < :name
            Predicate namelessThan = builder.lessThan(root.get("name"), predicates.get("name"));
            // p.name = :name
            Predicate nameEqual = builder.equal(root.get("name"), predicates.get("name"));

            // p.id < :id
            Predicate idLessThan = builder.lessThan(root.get("id"), UUID.fromString(predicates.get("id")));

            // p.name = :name AND p.id > :id
            Predicate and = builder.and(nameEqual, idLessThan);
            // (p.name > :name) OR (p.name = :name AND p.id > :id)
            return builder.or(namelessThan, and);
        };
    }

    /**
     * SELECT p.name, p.featured_media, p.vendor, p.id, p.type, p.status FROM
     * product p WHERE (p.createdDate > :createdDate) OR (p.createdDate =
     * :createdDate AND p.id > :id)
     * 
     * @apiNote Oldest first
     */
    public static Specification<AdminProduct> projectProductCreatedGreaterThan(Map<String, String> predicates) {
        return null;
    }

    /**
     * SELECT p.name, p.featured_media, p.vendor, p.id, p.type, p.status FROM
     * product p WHERE (p.createdDate < :createdDate) OR (p.createdDate =
     * :createdDate AND p.id < :id)
     * 
     * @apiNote Newest first
     */
    public static Specification<AdminProduct> projectProductCreatedLessThan(Map<String, String> predicates) {
        return null;
    }

    /**
     * SELECT p.name, p.featured_media, p.vendor, p.id, p.type, p.status FROM
     * product p WHERE (p.lastModifiedDate > :lastModifiedDate) OR
     * (p.lastModifiedDate = :lastModifiedDate AND p.id > :id)
     * 
     * @apiNote Oldest first
     */
    public static Specification<AdminProduct> projectProductModifiedGreaterThan(Map<String, String> predicates) {
        return null;
    }

    /**
     * SELECT p.name, p.featured_media, p.vendor, p.id, p.type, p.status FROM
     * product p WHERE (p.lastModifiedDate < :lastModifiedDate) OR
     * (p.lastModifiedDate = :lastModifiedDate AND p.id < :id)
     * 
     * @apiNote Newest first
     */
    public static Specification<AdminProduct> projectProductModifiedLessThan(Map<String, String> predicates) {
        return null;
    }

    /**
     * SELECT p.name, p.featured_media, p.vendor, p.id, p.type, p.status FROM
     * product p 
     * INNER JOIN product_category pc ON p.id = pc.product_id 
     * INNER JOIN category c ON pc.category_id = c.id WHERE c.id = :categoryId
     * 
     */
    public static <T extends AdminProduct> Specification<T> productInCategory(UUID categoryId) {
        return (root, query, builder) -> {
            Join<Product, ProductCategory> pc = root.join(Product_.CATEGORIES);
            Join<ProductCategory, Category> category = pc.join(ProductCategory_.CATEGORY);

            return builder.equal(category.get(Category_.ID), categoryId);
        };
    }

    /**
     * SELECT p.name, p.featured_media, p.vendor, p.id, p.type, p.status FROM
     * product p 
     * INNER JOIN product_category pc ON p.id = pc.product_id 
     * INNER JOIN category c ON pc.category_id = c.id WHERE c.friendly_uri_fragment = :categoryHandle
     * 
     */
    public static <T extends AdminProduct> Specification<T> productInCategoryHandle(URI categoryHandle) {
        return (root, query, builder) -> {
            Join<Product, ProductCategory> pc = root.join(Product_.CATEGORIES);
            Join<ProductCategory, Category> category = pc.join(ProductCategory_.CATEGORY);

            return builder.equal(category.get(Category_.FRIENDLY_URI_FRAGMENT), categoryHandle);
        };
    }

}
