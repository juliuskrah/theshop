package com.shoperal.core.business;

import static com.shoperal.core.utility.StringUtilities.replaceAllWhiteSpacesWithHypens;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.shoperal.core.business.exception.StoreItemNotFoundException;
import com.shoperal.core.dto.CategoryDTO;
import com.shoperal.core.dto.MediaDTO;
import com.shoperal.core.dto.MetaTag;
import com.shoperal.core.model.Category;
import com.shoperal.core.repository.CategoryRepository;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

/**
 * @author Julius Krah'
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
	private final CategoryRepository categoryRepository;

	@Builder
	private Category toEntity(CategoryDTO category) {
		var entity = new Category();
		entity.setDescription(category.getDescription());
		entity.setDisplayed(category.isDisplayed());
		entity.setEnabled(category.isEnabled());
		var friendlyUriFragment = URI.create(category.getFriendlyName());
		entity.setFriendlyUriFragment(friendlyUriFragment);
		var coverImage = category.getCoverImage();
		if (coverImage != null) {
			entity.setImageMediaType(coverImage.getMediaType());
			entity.setImageUri(coverImage.getSrc());
		}
		var thumbnail = category.getThumbnail();
		if (thumbnail != null) {
			entity.setThumbnailMediaType(thumbnail.getMediaType());
			entity.setThumbnailUri(thumbnail.getSrc());
		}
		entity.setName(category.getName());
		var metaTags = category.getMetaTags();
		if (!CollectionUtils.isEmpty(metaTags)) {
			var map = metaTags.stream().collect(Collectors.toMap(MetaTag::getName, MetaTag::getContent));
			if (map.containsKey("keywords"))
				entity.setMetaKeywords(map.get("keywords"));
			if (map.containsKey("title"))
				entity.setMetaTitle(map.get("title"));
			if (map.containsKey("description"))
				entity.setMetaDescription(map.get("description"));
		}
		return entity;
	}

	@Builder
	private CategoryDTO fromEntity(Category category) {
		var dto = new CategoryDTO();
		var name = category.getName();
		var metaTags = new ArrayList<MetaTag>();
		if (category.getImageUri() != null) {
			var coverImage = new MediaDTO(replaceAllWhiteSpacesWithHypens(name), category.getImageMediaType(),
					category.getImageUri());
			dto.setCoverImage(coverImage);
		}
		if (category.getThumbnailUri() != null) {
			var thumbnail = new MediaDTO(replaceAllWhiteSpacesWithHypens(name), category.getThumbnailMediaType(),
					category.getThumbnailUri());
			dto.setThumbnail(thumbnail);
		}
		if (category.getMetaDescription() != null) {
			metaTags.add(new MetaTag(null, category.getMetaDescription(), null, "description"));
		}
		if (category.getMetaKeywords() != null) {
			metaTags.add(new MetaTag(null, category.getMetaKeywords(), null, "keywords"));
		}
		if (category.getMetaTitle() != null) {
			metaTags.add(new MetaTag(null, category.getMetaTitle(), null, "title"));
		}
		dto.setId(category.getId());
		dto.setName(name);
		dto.setDescription(category.getDescription());
		dto.setDisplayed(category.isDisplayed());
		dto.setEnabled(category.isEnabled());
		dto.setFriendlyName(category.getFriendlyUriFragment().getPath());
		dto.setMetaTags(metaTags);
		category.getCreatedBy().ifPresent(dto::setCreatedBy);
		category.getLastModifiedBy().ifPresent(dto::setLastModifiedBy);
		category.getCreatedDate().ifPresent(date -> dto.setCreatedDate(date.toZonedDateTime()));
		category.getLastModifiedDate().ifPresent(date -> dto.setLastModifiedDate(date.toZonedDateTime()));
		return dto;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CategoryDTO findCategoryById(UUID id) {
		return categoryRepository.findById(id).map(this::fromEntity) //
				.orElseThrow(() -> new StoreItemNotFoundException("The requested category has been removed"));
	}

	@Override
	public Page<CategoryDTO> findCategoriesPaged() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CategoryDTO> findCategories() {
		return StreamSupport.stream(categoryRepository.findAll().spliterator(), false) //
				.map(this::fromEntity) //
				.collect(Collectors.toList());
	}

	@Override
	public Page<CategoryDTO> findCategoriesByDescriptionPaged(String description) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CategoryDTO> findCategoriesByDescription(String description) {
		return StreamSupport.stream(categoryRepository.findByDescription(description).spliterator(), false) //
				.map(this::fromEntity) //
				.collect(Collectors.toList());
	}

	@Override
	public Page<CategoryDTO> findCategoriesByNamePaged(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CategoryDTO> findCategoriesByName(String name) {
		return StreamSupport.stream(categoryRepository.findByNameContains(name).spliterator(), false) //
				.map(this::fromEntity) //
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	@Override
	public CategoryDTO createCategory(CategoryDTO category) {
		var entity = toEntity(category);
		entity = categoryRepository.save(entity);
		// TODO save menu thumbnails and products
		return new CategoryDTOBuilder().category(entity).build();
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public CategoryDTO modifyCategory(UUID id, CategoryDTO category) {
		return categoryRepository.findById(id).map(entity -> updateCategory(id, category)) //
				.orElseThrow(() -> new StoreItemNotFoundException(
						"The category you are attempting to update has been removed"));
	}

	private CategoryDTO updateCategory(UUID id, CategoryDTO category) {
		var entity = toEntity(category);
		entity.setId(id);
		categoryRepository.save(entity);
		category.setId(id);
		return category;
	}

}
