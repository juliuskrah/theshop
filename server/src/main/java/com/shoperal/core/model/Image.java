package com.shoperal.core.model;

import java.net.URI;
import java.util.UUID;

import lombok.Data;

/**
 * @author Julius Krah
 * @deprecated use {@link Media}
 */
@Data
@Deprecated
public class Image {
    private UUID id;
    private String alt;
    private URI src;
    private String contentType;
}
