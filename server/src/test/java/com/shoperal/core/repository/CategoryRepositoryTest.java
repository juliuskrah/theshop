package com.shoperal.core.repository;

import static java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OFFSET_DATE_TIME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Consumer;

import jakarta.persistence.PersistenceException;

import com.shoperal.core.config.JpaConfiguration;
import com.shoperal.core.model.Category;
import com.shoperal.core.model.Product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import lombok.RequiredArgsConstructor;

@TestConstructor(autowireMode = AutowireMode.ALL)
@RequiredArgsConstructor
@Sql(scripts = "classpath:scripts/category_data.sql", config = @SqlConfig(separator = org.springframework.jdbc.datasource.init.ScriptUtils.EOF_STATEMENT_SEPARATOR))
@DataJpaTest(includeFilters = { @Filter(type = ASSIGNABLE_TYPE, classes = { JpaConfiguration.class }) })
class CategoryRepositoryTest {
	private final CategoryRepository repository;
	private final TestEntityManager em;

	@Test
	@WithMockUser("julius")
	@DisplayName("Test audit infrastructure")
	void auditTest() {
		var category = new Category();
		category.setName("Motor Cycle");
		repository.save(category);
		var categoryOptional = repository.findById(category.getId());
		assertNotNull(categoryOptional);
		assertThat(categoryOptional).isPresent();
		assertThat(categoryOptional).get().extracting("createdDate").isNotNull();
		assertThat(categoryOptional).get().extracting("lastModifiedDate").isNotNull();
		assertThat(categoryOptional).get().extracting("createdBy").isNotNull();
		assertThat(categoryOptional).get().extracting("lastModifiedBy").isNotNull();

		Consumer<Category> auditRequirements = cat -> {
			assertThat(cat.getCreatedBy()).asString().contains("julius");
			assertThat(cat.getLastModifiedBy()).hasValue("julius");
			var now = OffsetDateTime.now();
			assertThat(cat.getCreatedDate()).get(as(OFFSET_DATE_TIME)).isEqualToIgnoringHours(now);
			assertThat(cat.getLastModifiedDate()).get(as(OFFSET_DATE_TIME)).isBefore(now);
		};
		assertThat(categoryOptional).get().satisfies(auditRequirements);
	}

	@Test
	@DisplayName("Test audit infrastructure when there's no principal")
	void auditWhenUserNotPresentTest() {
		var category = new Category();
		category.setName("Scooter");
		repository.save(category);
		var categoryOptional = repository.findById(category.getId());
		assertNotNull(categoryOptional);
		assertThat(categoryOptional).isPresent();
		assertThat(categoryOptional).get().extracting("createdBy").isNotNull();
		assertThat(categoryOptional).get().extracting("lastModifiedBy").isNotNull();

		Consumer<Category> auditRequirements = cat -> {
			assertThat(cat.getCreatedBy()).asString().contains("system");
			assertThat(cat.getLastModifiedBy()).hasValue("system");
		};
		assertThat(categoryOptional).get().satisfies(auditRequirements);
	}

	@Test
	@DisplayName("Test unique constriant on category name")
	void failOnDuplicateTest() {
		var category = new Category();
		category.setName("Blender");
		repository.save(category);
		assertThrows(PersistenceException.class, () -> em.flush());
	}

	@Test
	@DisplayName("Test inserted data")
	void insertedDataTest() {
		var count = repository.count();
		assertThat(count).isEqualTo(5L);
		var category = repository.findById(UUID.fromString("a27a3860-0e5d-461f-ab5e-4862ddd81834"));
		assertThat(category).isPresent();
		Consumer<Category> insertedDataRequirements = cat -> {
			assertThat(cat.isEnabled()).isFalse();
			assertThat(cat.getName()).contains("Desktop Computers");
			assertThat(cat.getDescription()).contains("computer is a personal computer designed",
					"personal computer designed for regular use");
			assertThat(cat.getLastModifiedDate()).get(as(OFFSET_DATE_TIME))
					.isEqualToIgnoringTimezone(OffsetDateTime.parse("2020-08-29T21:49:00+08:00", ISO_OFFSET_DATE_TIME));
		};
		assertThat(category).get().satisfies(insertedDataRequirements);
	}

	@Test
	@DisplayName("Test contains description")
	void descriptionContainsTest() {
		var count = repository.count();
		assertThat(count).isEqualTo(5L);
		var categories = repository.findByDescription("computer is a personal computer designed");
		assertThat(categories).hasSize(1);
		assertThat(categories).element(0).hasFieldOrPropertyWithValue("name", "Desktop Computers");
	}

	@Test
	@DisplayName("Test find by name containing")
	void nameContainsTest() {
		var categories = repository.findByNameContains("lender");
		assertThat(categories).hasSize(1);
		assertThat(categories).element(0).hasFieldOrPropertyWithValue("enabled", true);
	}

	@Test
	@DisplayName("Test find by name starting with")
	void nameStartsWithTest() {
		var categories = repository.findByNameStartsWith("Juice");
		assertThat(categories).hasSize(1);
		assertThat(categories).element(0).hasFieldOrPropertyWithValue("imageUri",
				URI.create("/thumbnail/categories/430f60b1-fa49-42bc-973e-cd49e296a185/juice-maker_thumb.jpg"));
	}

	@Test
	@DisplayName("Test find by name ending with")
	void nameEndingWithTest() {
		var categories = repository.findByNameEndsWith("aker");
		assertThat(categories).hasSize(1);
		assertThat(categories).element(0).hasFieldOrPropertyWithValue("imageUri",
				URI.create("/thumbnail/categories/430f60b1-fa49-42bc-973e-cd49e296a185/juice-maker_thumb.jpg"));
	}

	@Test
	void addProductToCategoryTest() {
		var category = repository.findById(UUID.fromString("a27a3860-0e5d-461f-ab5e-4862ddd81834"));
		assertThat(category).isPresent();
		var product = new Product();
		product.setCreatedBy("test");
		product.setName("HP Pavilon 6");
		product.setFriendlyUriFragment(URI.create("/product/pavilon-6"));
		product.setFeaturedMedia(URI.create("/images/products/pavilon-6.png"));
		em.persist(product);
		em.flush();

		category.get().addProduct(product);
		em.flush();

		var productFromDB = em.find(Product.class, product.getId());
		assertThat(productFromDB).isNotNull();
		assertThat(productFromDB.getCategories()).isNotEmpty();
		assertThat(productFromDB.getCategories()).element(0) //
			.hasFieldOrPropertyWithValue("category", category.get());
	}
}
