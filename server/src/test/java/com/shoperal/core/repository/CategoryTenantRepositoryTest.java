package com.shoperal.core.repository;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OFFSET_DATE_TIME;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import javax.sql.DataSource;

import com.shoperal.core.ApplicationTests;
import com.shoperal.core.model.Category;
import com.shoperal.core.tenancy.Settings;
import com.shoperal.core.tenancy.TenantContextHolder;
import com.shoperal.core.tenancy.TenantContextImpl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.test.context.support.WithMockUser;

import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Julius Krah
 */
@Slf4j
@TestInstance(Lifecycle.PER_CLASS)
@RequiredArgsConstructor
public class CategoryTenantRepositoryTest extends ApplicationTests {
	private final CategoryRepository repository;
	private final SpringLiquibase liquibase;
	private final DataSource dataSource;

	@BeforeAll 
	void beforeAll() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.setSeparator(org.springframework.jdbc.datasource.init.ScriptUtils.EOF_STATEMENT_SEPARATOR);
		// initialize tenant-one database
		setTenant("tenantOne");
		initializeTenantDatabase("scripts/tenant_one_data.sql", populator);

		// initialize tenant-two database
		setTenant("tenantTwo");
		initializeTenantDatabase("scripts/tenant_two_data.sql", populator);
	}

	@BeforeEach
	void beforeEach(TestInfo info) {
		String tag = info.getTags().stream().findFirst().orElse("none");
		setTenant(tag);
	}

	@Test
	@Tag("tenantOne")
	@WithMockUser("tenantOne User")
	@DisplayName("find by description for tenantOne")
	public void testFindByDescription() {
		var categories = repository.findByDescription("small tool that is designed for food-related");
		assertThat(categories).isNotEmpty();
	}

	@Test
	@Tag("tenantTwo")
	@WithMockUser("tenantTwo User")
	@DisplayName("find category by id")
	public void testFindById() {
		var category = repository.findById(UUID.fromString("dd02f30b-5068-410b-b3b7-272d44744c54"));
		assertThat(category).isPresent();
		Consumer<Category> insertedDataRequirements = cat -> {
			assertThat(cat.isEnabled()).isTrue();
			assertThat(cat.getName()).contains("Girl Toys");
			assertThat(cat.getMetaKeywords()).contains("children girls");
			assertThat(cat.getImageUri()).asString()
					.isEqualTo("/thumbnail/categories/dd02f30b-5068-410b-b3b7-272d44744c54/girl-toy_thumb.jpg");
			assertThat(cat.getLastModifiedDate()).get(as(OFFSET_DATE_TIME))
					.isEqualToIgnoringTimezone(OffsetDateTime.parse("2020-10-01T13:39:00+00:00", ISO_OFFSET_DATE_TIME));
		};
		assertThat(category).get().satisfies(insertedDataRequirements);
	}

	@AfterEach
	void afterEach() {
		TenantContextHolder.clearContext();
	}

	private void setTenant(String tenant) {
		var tenantContext = new TenantContextImpl();
		tenantContext.setSettings(new SettingsTest(tenant));
		TenantContextHolder.setContext(tenantContext);
	}

	private void initializeTenantDatabase(String script, ResourceDatabasePopulator populator) {
		try {
			liquibase.afterPropertiesSet();
		} catch (LiquibaseException e) {
			log.error(e.getLocalizedMessage());
		}
		populator.addScript(new ClassPathResource(script));
		populator.execute(this.dataSource);
		TenantContextHolder.clearContext();
	}

	static class SettingsTest implements Settings {
		private final String name;

		public SettingsTest(String name) {
			this.name = name;
		}

		@Override
		public UUID getTenantId() {
			return null;
		}

		@Override
		public String getTenantName() {
			return name;
		}
	}
}
