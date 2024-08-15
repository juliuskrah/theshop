package com.shoperal.core.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
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
public class OrderPayment implements Serializable {
    private static final long serialVersionUID = 6111126530739103436L;
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Order order;
    private Double amountPaid;
    private OffsetDateTime dateAdded;
}
