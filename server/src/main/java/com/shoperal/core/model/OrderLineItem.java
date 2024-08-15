package com.shoperal.core.model;

import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
@Entity
public class OrderLineItem implements Serializable {

    private static final long serialVersionUID = -6915584719431485063L;
    @Id
    private UUID id;
    private Integer quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
    /**
     * This is the price at the time the order was made
     */
    private Double price;
}
