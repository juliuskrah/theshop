package com.shoperal.core.model;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


import org.hibernate.annotations.Type;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.Data;

@Entity
@Table(name = "product_technical_seo")
@Data
public class SEO implements Serializable {

    private static final long serialVersionUID = -1770467146871552977L;
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String content;
    private String property;
    private String rel;
    private URI href;
    private String type = "application/ld+json";
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private StructuredLDData body;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
    @Enumerated(EnumType.STRING)
    private HtmlElement htmlElementType = HtmlElement.META;
}
