package com.shoperal.core.config;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import com.shoperal.core.repository.BaseRepositoryImpl;
import com.shoperal.core.repository.NoopRepository;
import com.shoperal.core.tenancy.TenantDataSource;
import com.zaxxer.hikari.HikariDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import io.micrometer.core.instrument.MeterRegistry;

/**
 * @author Julius Krah
 */
@Configuration(proxyBeanMethods = false)
@EnableJpaRepositories(repositoryBaseClass  = BaseRepositoryImpl.class, //
	basePackageClasses = NoopRepository.class)
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware", dateTimeProviderRef = "offsetDateTimeProvider")
public class JpaConfiguration {
	@Autowired
	private DataSourceProperties properties; 

	/**
	 * Set current auditor to System if the user is anonymous
	 * 
	 * @return Current auditor or system
	 */
	@Bean
	AuditorAware<String> springSecurityAuditorAware() {
		return () -> Optional.ofNullable(SecurityContextHolder.getContext()) //
				.map(SecurityContext::getAuthentication) //
				.filter(Authentication::isAuthenticated) //
				.map(Authentication::getName) //
				.or(() -> Optional.of("system"));

	}

	/**
	 * Provide an instance of OffsetDateTime for modified date and created date
	 * during auditing
	 * 
	 * @return OffsetDateTime Provider
	 */
	@Bean
	DateTimeProvider offsetDateTimeProvider() {
		return () -> Optional.of(OffsetDateTime.now());
	}

	// @Bean Multi-tenant
	DataSource tenantDataSource() {
		var dataSource = new TenantDataSource();
		dataSource.setLenientFallback(false);
		dataSource.setDefaultTargetDataSource(dataSource());
		dataSource.setTargetDataSources(Map.of());
		return dataSource;
	}
	
	private DataSource dataSource() {
		var dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
		dataSource.setPoolName(properties.getName());
		dataSource.addDataSourceProperty("socketTimeout", 10); // very important
		return dataSource;
	}
	
	/**
	 * We need to control pool size to ensure tenants don't use up the entire connection
	 * pool available to the database instance
	 * 
	 * @see https://github.com/brettwooldridge/HikariCP/blob/dev/documents/Welcome-To-The-Jungle.md
	 */
	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.hikari")
	DataSource dataSource(MeterRegistry meterRegistry) {
		var dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
		dataSource.setPoolName(properties.getName());
		dataSource.addDataSourceProperty("socketTimeout", 10); // very important
		dataSource.setMetricRegistry(meterRegistry);
		return dataSource;
	}
}
