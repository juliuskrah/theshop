package com.shoperal.core.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.shoperal.core.model.CurrencyCode;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class StoreDTO extends AuditingDTO {
    private UUID id;
    @NotBlank
    @Size(max = 100)
    private String name;
    private String url;
    @Size(max = 6144)
    private String description;
    private CurrencyCode currencyCode;
}
