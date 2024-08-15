package com.shoperal.core.model;

import java.io.Serializable;
import java.net.URI;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

import lombok.Data;

/**
 * Represents a media object
 * 
 * @author Julius Krah
 */
@Data
public class Media implements Serializable {

    private static final long serialVersionUID = -263164868776705570L;
    private UUID id;
    private String alt;
    private URI src;
    private URI previewImage;
    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    private MediaContentType mediaType;
}
