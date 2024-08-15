package com.shoperal.core.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 
 * @author Julius Krah
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CategoryDTO extends AuditingDTO {
	/**
	 * Specifies the category to update or create a new category if absent
	 */
	private UUID id;
	@NotBlank
	@Size(max = 100)
	private String name;
	@Size(max = 6144)
	private String description;
	/**
	 * A unique human-friendly string for the category
	 */
	@NotBlank
	@Size(max = 132)
	private String friendlyName;
	@Valid
	private MediaDTO coverImage;
	@Valid
	private MediaDTO thumbnail;
	private List<MediaDTO> menuThumbnail;
	private boolean enabled = true;
	private boolean displayed = false;
	private List<@Valid MetaTag> metaTags;
	/**
	 * Initial list of category products.
	 */
	private List<@NotNull UUID> products;
	private CategorySortOrder sortOrder;
}
