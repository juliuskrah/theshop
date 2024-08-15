package com.shoperal.core.dto;

import java.util.UUID;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
public class MenuDTO {
    private UUID id;
    private String title;
    private String handle;
}
