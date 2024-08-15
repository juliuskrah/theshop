package com.shoperal.core.repository;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.shoperal.core.model.Category;

/**
 * @author Julius Krah
 */
public interface CategoryRepository extends CrudRepository<Category, UUID> {

	@Query("SELECT c FROM #{#entityName} c WHERE c.description LIKE %?#{escape([0])}% escape ?#{escapeCharacter()}")
	Iterable<Category> findByDescription(String description);

	@Query("SELECT c FROM #{#entityName} c WHERE c.description LIKE %?#{escape([0])}% escape ?#{escapeCharacter()}")
	Page<Category> findByDescription(String description, Pageable pageable);

	Iterable<Category> findByNameContains(String name);

	Page<Category> findByNameContains(String name, Pageable pageable);

	Iterable<Category> findByNameStartsWith(String name);

	Page<Category> findByNameStartsWith(String name, Pageable pageable);

	Iterable<Category> findByNameEndsWith(String name);

	Page<Category> findByNameEndsWith(String name, Pageable pageable);

	Page<Category> findAll(Pageable pageable);

	@Query("SELECT COUNT(c) FROM ProductCategory pc JOIN pc.product p " //
			+ "JOIN pc.category c WHERE p.id = :productId")
	long countByProduct(UUID productId);
}
