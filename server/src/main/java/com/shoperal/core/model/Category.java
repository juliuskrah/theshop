package com.shoperal.core.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Julius Krah
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Category extends AbstractAuditEntity {
	private static final long serialVersionUID = 1L;
	private String name;
	@Lob
	private String description;
	private URI imageUri;
	private String imageMediaType;
	private URI thumbnailUri;
	private String thumbnailMediaType;
	private String metaTitle;
	private String metaDescription;
	private String metaKeywords;
	/**
	 * A unique human-friendly string of the category's name
	 */
	private URI friendlyUriFragment;
	private boolean enabled;
	private boolean displayed;
	@ManyToOne(optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_category", referencedColumnName = "id")
	private Category parent;
	@OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ProductCategory> products = new ArrayList<>();

	public void addProduct(Product product) {
		ProductCategory productCategory = new ProductCategory(this, product);
		products.add(productCategory);
		product.getCategories().add(productCategory);
	}

	public void removeProduct(Product product) {
		ProductCategory productCategory = new ProductCategory(this, product);
		product.getCategories().remove(productCategory);
		products.remove(productCategory);
		productCategory.setProduct(null);
		productCategory.setCategory(null);
	}

}
