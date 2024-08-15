package com.shoperal.core.dto;

import java.net.URI;
import java.util.UUID;

import lombok.Data;

/**
 * @author Julius
 */
@Data
public class MenuItemDTO {
    private UUID id;
    private String title;
    private URI url;
    private int position;
}
