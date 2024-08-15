package com.shoperal.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;

import com.shoperal.core.dto.Pair;
import com.shoperal.core.tenancy.TenantDataSource;
import com.shoperal.core.tenancy.TenantDatabaseConnection;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

@TestConstructor(autowireMode = AutowireMode.ALL)
@Import(ApplicationTests.Configuration.class)
@SpringBootTest
abstract public class ApplicationTests {

	@TestConfiguration(proxyBeanMethods = false)
	static class Configuration {
		@Autowired
		private DataSourceProperties properties;

		@Bean
		@Primary
		DataSource testDataSource() {
			var dataSource = new TenantDataSource();
			dataSource.setLenientFallback(false);
			dataSource.setDefaultTargetDataSource(mainDataSource());
			dataSource.setTargetDataSources(targetDataSources());
			return dataSource;
		}

		private Pair<String, DataSource> buildDataSource(TenantDatabaseConnection tenant) {
			var properties = new Properties();
			properties.putAll(tenant.getDataSourceProperties());
			properties.putAll(tenant.getHealthCheckProperties());
			var config = new HikariConfig(properties);
			config.setPoolName(tenant.getPoolName());

			if (tenant.getDataSourceClassName() != null) {
				config.setDataSourceClassName(tenant.getDataSourceClassName());
			} else {
				config.setJdbcUrl(tenant.getUrl());
				config.setUsername(tenant.getUsername());
				config.setPassword(tenant.getPassword());
			}

			var hikariDataSource = new HikariDataSource(config);
			return Pair.with(tenant.getTenantName(), hikariDataSource);
		}

		private HikariDataSource mainDataSource() {
			var dataSource = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();
			dataSource.setPoolName(properties.getName());
			return dataSource;
		}

		private Map<Object, Object> targetDataSources() {
			return tenants().stream().map(this::buildDataSource) //
					.collect(Collectors.toMap(Pair::getValue0, Pair::getValue1));
		}

		private Set<TenantDatabaseConnection> tenants() {
			var properties = new HashMap<Object, Object>();
			properties.put("dataSource.user", "sa");
			properties.put("dataSource.password", "");
			properties.put("dataSource.loginTimeout", 5);
			properties.put("dataSource.databaseName", "jdbc:hsqldb:mem:tenantOne");
			var tenantOne = TenantDatabaseConnection.builder() //
					.tenantName("tenantOne") //
					.poolName("TenantOnePool") //
					.dataSourceClassName("org.hsqldb.jdbc.JDBCDataSource") //
					.dataSourceProperties(properties).build();

			var tenantTwo = TenantDatabaseConnection.builder().tenantName("tenantTwo") //
					.url("jdbc:hsqldb:mem:tenantTwo").password("").username("sa").build();

			return Set.of(tenantOne, tenantTwo);
		}

	}
}
