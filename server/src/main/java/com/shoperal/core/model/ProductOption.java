package com.shoperal.core.model;

import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

import lombok.Data;

/**
 * Custom product property names like "Size", "Color", and "Material". Products
 * are based on permutations of these options. A product may have a maximum of 3
 * options. 255 characters limit each
 * 
 * @author Julius Krah
 */
@Data
public class ProductOption implements Serializable {

    private static final long serialVersionUID = 8274159294136864488L;
    private UUID id;
    private String name;
    /**
     * The product option's position
     */
    private Integer position;
    /**
     * The corresponding value to the product option name
     */
    private Set<String> values;
}
