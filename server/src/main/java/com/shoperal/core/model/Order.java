package com.shoperal.core.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;

import org.hibernate.annotations.LazyToOne;
import org.hibernate.annotations.LazyToOneOption;

import lombok.Data;
import lombok.EqualsAndHashCode;
/**
 * @author Julius Krah
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class Order  extends AbstractAuditEntity {

    private static final long serialVersionUID = 7715978997343002625L;
    /**
     * The sales channel the order was made from (online store, draft order or sales app)
     */
    private String channel;
    private Long orderNumber;
    private String customer;
    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus payment;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Column(name = "fulfillment_status")
    @Enumerated(EnumType.STRING)
    private FulfillmentStatus fulfillment;
    @OneToOne(
        mappedBy = "order",
        fetch = FetchType.LAZY,
        cascade = CascadeType.ALL
    )
    @LazyToOne(LazyToOneOption.NO_PROXY)
    private DraftOrder draft;
}
