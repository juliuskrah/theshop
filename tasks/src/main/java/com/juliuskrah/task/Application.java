package com.juliuskrah.task;

import java.util.Arrays;

import javax.naming.Context;
import javax.sql.DataSource;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;

import liquibase.integration.spring.MultiTenantSpringLiquibase;
import lombok.RequiredArgsConstructor;

@EnableTask
@SpringBootApplication
@RequiredArgsConstructor
@EnableConfigurationProperties(LiquibaseProperties.class)
public class Application {
	private final LiquibaseProperties properties;

	public static void main(String[] args) {
		System.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
		System.setProperty(Context.URL_PKG_PREFIXES, "org.apache.naming");
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner bootStrap() {
		return (args) -> System.out.println("args " + Arrays.toString(args));
	}

	//@Bean
	MultiTenantSpringLiquibase liquibase(DataSource dataSource) {
		var liquibase = new MultiTenantSpringLiquibase();
		liquibase.setSchemas(TenantUtilities.SERVICES);
		liquibase.setChangeLog(this.properties.getChangeLog());
		liquibase.setContexts(this.properties.getContexts());
		liquibase.setDefaultSchema(this.properties.getDefaultSchema());
		liquibase.setLiquibaseSchema(this.properties.getLiquibaseSchema());
		liquibase.setLiquibaseTablespace(this.properties.getLiquibaseTablespace());
		liquibase.setDatabaseChangeLogTable(this.properties.getDatabaseChangeLogTable());
		liquibase.setDatabaseChangeLogLockTable(this.properties.getDatabaseChangeLogLockTable());
		liquibase.setDropFirst(this.properties.isDropFirst());
		liquibase.setShouldRun(this.properties.isEnabled());
		liquibase.setLabels(this.properties.getLabels());
		liquibase.setRollbackFile(this.properties.getRollbackFile());
		liquibase.setDataSource(dataSource);
		return liquibase;
	}

}
