package com.shoperal.core.repository;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import com.shoperal.core.model.Category;
import com.shoperal.core.model.CategoryMenuThumbnail;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Julius Krah
 */
public interface CategoryMenuThumbnailRepository extends CrudRepository<CategoryMenuThumbnail, UUID> {
    Optional<CategoryMenuThumbnail> findByUri(URI uri);

    Iterable<CategoryMenuThumbnail> findByCategory(Category category);

    Iterable<CategoryMenuThumbnail> findByCategory(Category category, Sort sort);

    Iterable<CategoryMenuThumbnail> findByMediaType(String mediaType);

    @Query("FROM CategoryMenuThumbnail cmt WHERE cmt.category = :category and cmt.mediaType = :mediaType")
    Iterable<CategoryMenuThumbnail> findByCategoryMediaType(Category category, String mediaType);
}
