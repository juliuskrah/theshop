package com.shoperal.core.dto;

import java.net.URI;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Value;

@Value
public class MediaDTO {
	@Size(max = 60)
	String altText;
	@Size(max = 100)
	String mediaType;
	String encoding = "UTF-8";
	@NotNull
	URI src;
}
