package com.juliuskrah.task;

import static com.juliuskrah.task.TenantUtilities.JNDI_BASE;
import static com.juliuskrah.task.TenantUtilities.JNDI_TEMPLATE;
import static com.juliuskrah.task.TenantUtilities.resolveTemplate;

import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.liquibase.DataSourceClosingSpringLiquibase;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.cloud.task.listener.TaskExecutionListener;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
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
	}

	@Override
	public void onTaskEnd(TaskExecution taskExecution) {
		InitialContext context = null;
		try {
			var sql = "SELECT name, database_username, database_password, database_url FROM tenant";
			var tenants = fetchAvailableTenants(sql);
			context = new InitialContext();
			context.createSubcontext("java:");
			context.createSubcontext("java:comp");
			context.createSubcontext("java:comp/env");
			context.createSubcontext(JNDI_BASE);

			for (var tenant : tenants) {
				var jndiUrl = TenantUtilities.resolveTemplate(tenant.getName(), TenantUtilities.JNDI_TEMPLATE);
				var dataSource = createDataSource(tenant);
				context.bind(jndiUrl, dataSource);
			}
			context.bind(resolveTemplate("master", JNDI_TEMPLATE), ((JdbcTemplate) jdbcTemplate).getDataSource());

			for (var service : TenantUtilities.SERVICES) {
				initializeDatabases(service);
			}

			log.debug("tenants found {}", tenants.size());
		} catch (NamingException e) {
			log.warn("Cannot create remote registry", e);
		} finally {
			try {
				if (null != context)
					context.close();
			} catch (NamingException e) {
				log.warn("Cannot close context", e);
			}
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

	private void initializeDatabases(String service) {
		var liquibase = new MultiTenantSpringLiquibase();
		liquibase.setDropFirst(liquibaseProperties.isDropFirst());
		liquibase.setShouldRun(liquibaseProperties.isEnabled());
		liquibase.setJndiBase(TenantUtilities.JNDI_BASE);
		liquibase.setChangeLog(this.liquibaseProperties.getChangeLog());
		liquibase.setDefaultSchema(service);
		liquibase.setResourceLoader(resourceLoader);
		liquibase.setLabels(service);
		liquibase.setContexts(service);
		try {
			liquibase.afterPropertiesSet();
		} catch (Exception e) {
			log.warn("Cannot perform migration", e);
		}

	}

	private List<Tenant> fetchAvailableTenants(String sql) {
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			var tenant = new Tenant();
			tenant.setName(rs.getString("name"));
			tenant.setDatabaseUsername(rs.getString("database_username"));
			tenant.setDatabasePassword(rs.getString("database_password"));
			tenant.setDatabaseUrl(rs.getString("database_url"));
			return tenant;
		});
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
	}

	private DataSource createDataSource(Tenant tenant) {
		return DataSourceBuilder.create() //
				.url(tenant.getDatabaseUrl()) //
				.username(tenant.getDatabaseUsername()) //
				.password(tenant.getDatabasePassword()) //
				.build();
	}

}
