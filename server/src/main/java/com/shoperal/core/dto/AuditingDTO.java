package com.shoperal.core.dto;

import java.time.ZonedDateTime;

import lombok.Data;

@Data
public abstract class AuditingDTO {
    private ZonedDateTime createdDate;
	private ZonedDateTime lastModifiedDate;
	private String createdBy;
	private String lastModifiedBy;
}
