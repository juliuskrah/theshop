package com.shoperal.core.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Value;

@Value
public class MetaTag {
	String charset;
	@Size(max = 2048)
	@NotBlank
	String content;
	String httpEquiv;
	@NotBlank
	String name;
}
