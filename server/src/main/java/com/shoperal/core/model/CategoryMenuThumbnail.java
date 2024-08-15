package com.shoperal.core.model;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

import lombok.Data;

@Data
@Entity
public class CategoryMenuThumbnail implements Serializable {
    
    private static final long serialVersionUID = 2824719011636014142L;
    @Id
    @GeneratedValue
    private UUID id;
    private String alt;
    private String mediaType;
    private Integer priority;
    private URI uri;
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
}
