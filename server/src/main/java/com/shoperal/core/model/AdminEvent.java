package com.shoperal.core.model;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import com.fasterxml.jackson.databind.JsonNode;

import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;

import lombok.Data;

/**
 * The timeline for the order
 * 
 * @author Julius Krah
 */
@Data
@Entity
public class AdminEvent implements Serializable {
    private static final long serialVersionUID = 7162195200327720113L;
    @Id
    private UUID id;
    @Enumerated(EnumType.STRING)
    private OrderEventType type;
    private OffsetDateTime eventDate;
    /**
     * The details contained for an event object depends on the event object
     */
    @Enumerated(EnumType.STRING)
    private EventObject object;
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private JsonNode details;
}
