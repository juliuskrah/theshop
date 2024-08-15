package com.shoperal.core;

import java.time.Duration;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.core.io.Resource;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.cors.CorsConfiguration;

import lombok.Data;

/**
 * @author Julius Krah
 */
@Data
@Validated
@ConfigurationProperties(prefix = "shoperal", ignoreUnknownFields = false)
public class ApplicationProperties {
	private Resource fileStoreBaseLocation;
	@NestedConfigurationProperty
	private final CorsConfiguration cors = new CorsConfiguration();
	@NestedConfigurationProperty
	private final @Valid S3 s3 = new S3();
	@NestedConfigurationProperty
	private final @Valid Tenant tenant = new Tenant();

	@Data 
	public static class S3 {
		@NotBlank
		private String awsAccessKeyId;
		@NotBlank
		private String awsSecretAccessKey;
		@NotBlank
		private String awsHost = "shoperaldev.s3.eu-west-1.amazonaws.com";
		private String awsRegion = "eu-west-1";
		private String awsService = "s3";
		private Duration presignedURIValidity = Duration.ofMinutes(1);
	}

	@Data
	public static class Tenant {
		@NotBlank
		private String name;
		@Email
		@NotBlank
		private String email;
		@NotBlank
		private String currency;
		@NotBlank
		private String ianaTimezone;
		@NotBlank
		private String shoperalDomain;
		@NestedConfigurationProperty
		private final Limits limits = new Limits();
		@NestedConfigurationProperty
		private final Theme theme = new Theme();

		@Data
		public static class Limits {
			/**
			 * Upload size of product media in bytes. Default is 5MB
			 */
			private DataSize productMediaMaxFileSize = DataSize.ofBytes(5242880);
			/**
			 * Total media that can be uploaded per product
			 */
			private Short productMediaMaxCount = 5;
			/**
			 * Upload size of category media in bytes
			 * 
			 * Default is 5MB
			 */
			private DataSize categoryMediaMaxFileSize = DataSize.ofBytes(5242880);
			/**
			 * Total media that can be uploaded per category
			 */
			private Short categoryMediaMaxCount = 5;
		}

		@Data
		public static class Theme {
			private boolean enable = false;
			private String prefix;
			private String suffix = ".html";
		}

	}

}
