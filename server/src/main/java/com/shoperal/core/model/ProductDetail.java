package com.shoperal.core.model;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;


import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.array.ListArrayType;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
@Entity
public class ProductDetail implements Serializable {
    private static final long serialVersionUID = -2948870585158386431L;

    @Id
    private UUID id;
    @Column(columnDefinition = "text")
    private String description;
    @MapsId
    @JoinColumn(name = "id")
    @OneToOne
    private Product product;
    /**
     * The product type specified by the merchant
     */
    private String type;
    /**
     * The name of the product's vendor
     */
    private String vendor;
    @Enumerated(EnumType.STRING)
    private ProductStatus status = ProductStatus.DRAFT;
    private Double costPrice;
    @Type(ListArrayType.class)
    @Column(columnDefinition = "varchar[]")
    private List<String> tags;
}
