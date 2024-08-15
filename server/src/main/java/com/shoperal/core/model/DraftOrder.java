package com.shoperal.core.model;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToOne;

import com.fasterxml.jackson.databind.JsonNode;

import io.hypersistence.utils.hibernate.type.array.ListArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;

import org.hibernate.annotations.Type;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Draft orders are an excellent way to send a quote or allow customers to pay for a
 * telephone transaction without providing credit card details over the phone.
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = false)
public class DraftOrder extends AbstractAuditEntity {

    private static final long serialVersionUID = 6212811935023982564L;
    private Long orderNumer;
    @Type(ListArrayType.class)
    @Column(columnDefinition = "varchar[]")
    private List<String> tags;
    @Enumerated(EnumType.STRING)
    private DraftOrderStatus status;
    private OffsetDateTime orderCreatedDate;
    @OneToOne(fetch = FetchType.LAZY)
    private Order order;
    /**
     * The details of the order will contain information such as
     * - Line items
     * - Customer details
     */
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode details;
}
