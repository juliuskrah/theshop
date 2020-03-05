package com.juliuskrah.task;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.task.listener.TaskExecutionListener;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Component;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.MultiTenantSpringLiquibase;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@AllArgsConstructor
public class TheShopTaskExecutionListener implements TaskExecutionListener, ResourceLoaderAware {
	private final JdbcOperations jdbcTemplate;
	private final LiquibaseProperties liquibaseProperties;
	private ResourceLoader resourceLoader;

	@Override
	public void onTaskStartup(TaskExecution taskExecution) {
		log.info("Task has commerced");
		InitialContext context = null;
		var dataSource = DataSourceBuilder.create().url("jdbc:postgresql://localhost/master").username("master")
				.password("master").build();
		var dataSource1 = DataSourceBuilder.create().url("jdbc:postgresql://localhost/dulcet").username("dulcet")
				.password("dulcet").build();
		var dataSource2 = DataSourceBuilder.create().url("jdbc:postgresql://localhost/aparel").username("aparel")
				.password("aparel").build();
		try {

			context = new InitialContext();
			context.createSubcontext("java:");
			context.createSubcontext("java:comp");
			context.createSubcontext("java:comp/env");
			context.createSubcontext("java:comp/env/jdbc");
			context.bind("java:comp/env/jdbc/master", dataSource);
			context.bind("java:comp/env/jdbc/dulcet", dataSource1);
			context.bind("java:comp/env/jdbc/aparel", dataSource2);
		} catch (NamingException e) {
			log.warn("Cannot create remote registry", e);
		} finally {
			try {
				context.close();
			} catch (NamingException e) {
				log.warn("Cannot close context", e);
			}
		}

	}

	@Override
	public void onTaskEnd(TaskExecution taskExecution) {
//		var sql = "SELECT name, database_username, database_password, database_url FROM tenant";
//		var tenants = jdbcTemplate.query(sql, (rs, rowNum) -> {
//			var tenant = new Tenant();
//			tenant.setName(rs.getString("name"));
//			tenant.setDatabaseUsername(rs.getString("database_username"));
//			tenant.setDatabasePassword(rs.getString("database_password"));
//			tenant.setDatabaseUrl(rs.getString("database_url"));
//			return tenant;
//		});
//		for (var tenant : tenants) {
//			initializeDatabase(tenant.getDatabaseUrl(), tenant.getDatabaseUsername(), tenant.getDatabasePassword());
//		}
//		log.debug("tenants {}", tenants);

		var liquibase = new MultiTenantSpringLiquibase();
		liquibase.setDropFirst(liquibaseProperties.isDropFirst());
		liquibase.setShouldRun(liquibaseProperties.isEnabled());
		liquibase.setJndiBase("java:comp/env/jdbc");
		liquibase.setChangeLog(this.liquibaseProperties.getChangeLog());
		liquibase.setDefaultSchema("catalogs");
		liquibase.setResourceLoader(resourceLoader);
		liquibase.setLabels("catalogs");
		liquibase.setContexts("catalogs");
		try {
			liquibase.afterPropertiesSet();
		} catch (Exception e) {
			log.warn("Cannot perform migration", e);
		}
		log.info("Task has ended");
	}

	@Override
	public void onTaskFailed(TaskExecution taskExecution, Throwable throwable) {
		log.error("Task has failed");
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	private void initializeDatabase(String url, String user, String password) {
		var dataSource = DataSourceBuilder.create().url(url).username(user).password(password).build();
		var liquibase = new DataSourceClosingSpringLiquibase();
		liquibase.setDropFirst(liquibaseProperties.isDropFirst());
		liquibase.setShouldRun(liquibaseProperties.isEnabled());
		liquibase.setDataSource(dataSource);
		liquibase.setChangeLog(this.liquibaseProperties.getChangeLog());
		liquibase.setDefaultSchema("catalogs");
		liquibase.setResourceLoader(resourceLoader);
		liquibase.setLabels("catalogs");
		liquibase.setContexts("catalogs");
		try {
			liquibase.afterPropertiesSet();
		} catch (LiquibaseException e) {
			log.warn("Cannot perform migration", e);
		}

		// liquibase.setLabels(this.properties.getLabels());

	}

}
