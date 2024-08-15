package com.shoperal.core.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class ProductDTO extends AuditingDTO {
    private UUID id;
    @NotBlank
    @Size(max = 100)
    private String name;
    @Size(max = 6144)
    private String description;
    private Float price;
    @NotBlank
    private String mediaSrc;
    private String status;
    /**
     * This defaults to the name of the store
     */
    private String vendor;
    private String type;
    /**
     * A unique human-friendly string for the category
     */
    @NotBlank
    @Size(max = 132)
    @Pattern(regexp = "^[a-z0-9]+(-[a-z0-9]+)*$")
    private String friendlyName;
    /**
     * Whether the product has only a single variant with the default option and
     * value
     */
    private boolean onlyDefaultVariant;
    /**
     * Whether the product has out of stock variants
     */
    private boolean outOfStockVariants;
}
