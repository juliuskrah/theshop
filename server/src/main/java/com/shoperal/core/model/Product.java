package com.shoperal.core.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;


import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class Product extends AbstractAuditEntity {

    private static final long serialVersionUID = -278933209564551719L;
    private String name;
    @Column(columnDefinition = "text")
    private String description;
    /**
     * This is the URL of either an Image or a Video. The content type will determine whether to load {@code &lt;img&gt;} or <video>
     */
    private URI featuredMedia;
    /**
	 * A unique human-friendly string of the product's name
	 */
    private URI friendlyUriFragment;
    /**
     * The product type specified by the merchant
     */
    private String type;
    /**
     * The name of the product's vendor
     */
    private String vendor;
    @OneToMany(mappedBy = "product")
    private Set<SEO> seo;
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.DRAFT;
    private boolean inStock = true;
    /**
     * Usually less than {@link #comparedPrice}
     */
    private Double price;
    /**
     * When running a sale or promotion, the original price is moved here
     */
    private Double comparedPrice;
    @Type(ListArrayType.class)
    @Column(columnDefinition = "varchar[]")
    private List<String> tags;
    @OneToMany(
		mappedBy = "product",
		cascade = CascadeType.ALL,
		orphanRemoval = true
	)
    private List<ProductCategory> categories = new ArrayList<>();
    /**
     * A list of custom product options (maximum of 3 per product)
     */
    // private Set<ProductOption> options;
    // private Set<Media> media;
    // private Set<ProductVariant> variants;
}
