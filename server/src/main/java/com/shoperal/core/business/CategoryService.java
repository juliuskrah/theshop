package com.shoperal.core.business;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import com.shoperal.core.dto.CategoryDTO;

import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;

/**
 * The service class contains methods that handle the business operations and
 * rules for Shoperal Category.
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
public interface CategoryService {
	CategoryDTO findCategoryById(UUID id);

	Page<CategoryDTO> findCategoriesPaged();

	List<CategoryDTO> findCategories();

	Page<CategoryDTO> findCategoriesByDescriptionPaged(String description);

	List<CategoryDTO> findCategoriesByDescription(String description);

	Page<CategoryDTO> findCategoriesByNamePaged(String name);

	List<CategoryDTO> findCategoriesByName(String name);

	/**
	 * Creates a new category and saves menu thumbnails associated with this
	 * category
	 * <p>
	 * Optional initializes this categories with products
	 * 
	 * @param category new category
	 * @return
	 */
	CategoryDTO createCategory(@Valid CategoryDTO category);

	/**
	 * Updates the category
	 * 
	 * @param id       the id of the category to be updated
	 * @param category existing category
	 * @return the updated category
	 */
	CategoryDTO modifyCategory(UUID id, @Valid CategoryDTO category);
}
