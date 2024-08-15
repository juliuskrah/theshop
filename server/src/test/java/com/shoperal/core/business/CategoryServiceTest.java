package com.shoperal.core.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;

import com.shoperal.core.business.exception.StoreItemNotFoundException;
import com.shoperal.core.dto.CategoryDTO;
import com.shoperal.core.dto.MediaDTO;
import com.shoperal.core.dto.MetaTag;
import com.shoperal.core.model.Category;
import com.shoperal.core.repository.CategoryRepository;

import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@TestInstance(Lifecycle.PER_CLASS)
class CategoryServiceTest {
	private ApplicationContextRunner runner;
	@Mock
	private CategoryRepository categoryRepository;
	@InjectMocks
	private CategoryServiceImpl categoryService;
	private AutoCloseable closeable;

	@BeforeAll
	void init() {
		closeable = MockitoAnnotations.openMocks(this);
		runner = new ApplicationContextRunner() //
				.withBean(LocalValidatorFactoryBean.class) //
				.withUserConfiguration(Configuration.class)
				.withBean(CategoryServiceImpl.class, this.categoryRepository);
	}

	@AfterAll
	void releaseMocks() throws Exception {
		closeable.close();
	}

	private List<Category> categories() {
		var categories = new ArrayList<Category>();
		var category1 = new Category();
		category1.setCreatedBy("juliuskrah");
		category1.setFriendlyUriFragment(URI.create("/categories/category1"));
		category1.setDescription("category one description");
		category1.setName("category one");
		category1.setCreatedBy("dummy");
		category1.setLastModifiedBy("dummy");
		category1.setCreatedDate(OffsetDateTime.now());
		category1.setLastModifiedDate(OffsetDateTime.now());
		categories.add(category1);

		var category2 = new Category();
		category2.setFriendlyUriFragment(URI.create("/categories/category2"));
		category2.setLastModifiedBy("juliuskrah");
		category2.setDisplayed(true);
		category2.setName("category two");
		category2.setCreatedBy("dummy");
		category2.setLastModifiedBy("dummy");
		category2.setCreatedDate(OffsetDateTime.now());
		category2.setLastModifiedDate(OffsetDateTime.now());
		categories.add(category2);

		var category3 = new Category();
		category3.setFriendlyUriFragment(URI.create("/categories/category3"));
		category3.setEnabled(true);
		category3.setImageMediaType("image/jpg");
		category3.setImageUri(URI.create("/files/images/category3.jpeg"));
		category3.setName("category three");
		category3.setCreatedBy("dummy");
		category3.setLastModifiedBy("dummy");
		category3.setCreatedDate(OffsetDateTime.now());
		category3.setLastModifiedDate(OffsetDateTime.now());
		categories.add(category3);
		return categories;
	}

	@Test
	@DisplayName("when category is saved then name must not be blank (French)")
	void testSaveCategoryNameValidation() {
		LocaleContextHolder.setLocale(Locale.FRENCH);
		var category = new CategoryDTO();
		category.setName("");
		category.setFriendlyName("/categories/test_category");
		runner.run(context -> {
			var categoryService = context.getBean(CategoryService.class);
			var throwable = catchThrowable(() -> categoryService.createCategory(category));
			assertThat(throwable).isInstanceOf(ConstraintViolationException.class) //
					.hasMessageContaining("category.name: ne doit pas être vide");
		});
		LocaleContextHolder.resetLocaleContext();
	}

	@Test
	void testSaveCategoryFriendlyNameValidation() {
		var category = new CategoryDTO();
		category.setName("test category");
		category.setFriendlyName(
				"/categories/some/extremely/long/name/categories/some/extremely/long/name/categories/some/extremely/long/name/categories/some/extremely/long/name");
		runner.run(context -> {
			var categoryService = context.getBean(CategoryService.class);
			var throwable = catchThrowable(() -> categoryService.createCategory(category));
			assertThat(throwable).isInstanceOf(ConstraintViolationException.class) //
					.hasMessageContaining("category.friendlyName: size must be between 0 and 132");
		});
	}

	@Test
	void testSaveCategoryCoverImageValidation() {
		var category = new CategoryDTO();
		category.setName("test category");
		category.setFriendlyName("/categories/test-category");
		var coverImage = new MediaDTO("test-category", "image/gif", null);
		category.setCoverImage(coverImage);
		runner.run(context -> {
			var categoryService = context.getBean(CategoryService.class);
			var throwable = catchThrowable(() -> categoryService.createCategory(category));
			assertThat(throwable).isInstanceOf(ConstraintViolationException.class) //
					.hasMessageContaining("category.coverImage.src: must not be null");
		});
	}

	@Test
	void testSaveCategoryMetaTagsValidation() {
		var category = new CategoryDTO();
		category.setName("test category");
		category.setFriendlyName("/categories/test-category");
		var coverImage = new MediaDTO("test-category", "image/gif", URI.create("/files/images/test-category.gif"));
		category.setCoverImage(coverImage);
		var metaTags = List.of(new MetaTag(null, "meta description", null, ""));
		category.setMetaTags(metaTags);
		runner.run(context -> {
			var categoryService = context.getBean(CategoryService.class);
			var throwable = catchThrowable(() -> categoryService.createCategory(category));
			assertThat(throwable).isInstanceOf(ConstraintViolationException.class) //
					.hasMessageContaining("category.metaTags[0].name: must not be blank");
		});
	}

	@Test
	void testSaveCategory() {
		var category = new CategoryDTO();
		var entity = new Category();
		var metaTags = new ArrayList<MetaTag>();
		metaTags.add(new MetaTag(null, "meta description", null, "description"));
		category.setName("test category");
		category.setFriendlyName("/categories/test-category");
		category.setMetaTags(metaTags);
		
		entity.setFriendlyUriFragment(URI.create("/categories/test-category"));
		entity.setCreatedBy("dummy");
		entity.setLastModifiedBy("dummy");
		entity.setCreatedDate(OffsetDateTime.now());
		entity.setLastModifiedDate(OffsetDateTime.now());
		when(categoryRepository.save(any(Category.class))).thenReturn(entity);
		var capture = ArgumentCaptor.forClass(Category.class);
		categoryService.createCategory(category);
		verify(categoryRepository).save(capture.capture());
		assertThat(capture.getValue()).hasFieldOrPropertyWithValue("friendlyUriFragment",
				URI.create("/categories/test-category"));
		assertThat(capture.getValue()).hasFieldOrPropertyWithValue("metaDescription", "meta description");
	}

	@Test
	void testUpdateCategoryCoverImageValidation() {
		var category = new CategoryDTO();
		category.setName("test category");
		category.setFriendlyName("/categories/test-category");
		var coverImage = new MediaDTO("test-category", "image/gif", null);
		category.setCoverImage(coverImage);
		runner.run(context -> {
			var categoryService = context.getBean(CategoryService.class);
			var throwable = catchThrowable(() -> categoryService.modifyCategory(UUID.randomUUID(), category));
			assertThat(throwable).isInstanceOf(ConstraintViolationException.class) //
					.hasMessageContaining("category.coverImage.src: must not be null");
		});
	}

	@Test
	void testUpdateCategory() {
		var uuid = UUID.fromString("7b74abc2-3359-49ba-8549-8f47226d622b");
		var categoryRepository = when(mock(CategoryRepository.class).findById(any(UUID.class))) //
				.thenAnswer(invocation -> {
					UUID id = invocation.getArgument(0);
					if (uuid.equals(id))
						return Optional.empty();
					return Optional.of(new Category());
				}).<CategoryRepository>getMock();
		var category = new CategoryDTO();
		var metaTags = new ArrayList<MetaTag>();
		metaTags.add(new MetaTag(null, "meta description", null, "description"));
		category.setName("test category");
		category.setFriendlyName("/categories/test-category");
		category.setMetaTags(metaTags);
		var categoryService = new CategoryServiceImpl(categoryRepository);

		var tested = categoryService.modifyCategory(UUID.randomUUID(), category);
		assertThat(tested).isNotNull();
		assertThat(tested).hasFieldOrPropertyWithValue("metaTags", metaTags);

		var throwable = catchThrowableOfType(() -> categoryService.modifyCategory(uuid, category),
				StoreItemNotFoundException.class);
		assertThat(throwable).hasMessageEndingWith("to update has been removed");
		assertThat(throwable).hasNoCause();
	}

	@Test
	void testFindById() {
		var category = new Category();
		category.setDescription("Home Appliance Description");
		category.setDisplayed(true);
		category.setEnabled(true);
		category.setFriendlyUriFragment(URI.create("/categories/home-appliance"));
		category.setImageMediaType("image/png");
		category.setImageUri(URI.create("/files/images/home-appliance.png"));
		category.setMetaDescription("Home Appliance meta description");
		category.setMetaKeywords("appliances home-appliances home");
		category.setMetaTitle("Home Appliance");
		category.setName("home appliance");
		category.setThumbnailMediaType("image/gif");
		category.setThumbnailUri(URI.create("/files/images/thumbnail/home-appliance.gif"));
		category.setCreatedBy("dummy");
		category.setLastModifiedBy("dummy");
		category.setCreatedDate(OffsetDateTime.now());
		category.setLastModifiedDate(OffsetDateTime.now());
		when(this.categoryRepository.findById(any(UUID.class))).thenReturn(Optional.of(category));
		var dto = this.categoryService.findCategoryById(UUID.randomUUID());

		// begin assertions
		assertThat(dto).isNotNull();
		assertThat(dto).asInstanceOf(InstanceOfAssertFactories.type(CategoryDTO.class)) //
				.hasFieldOrPropertyWithValue("name", category.getName());
		assertThat(dto).asInstanceOf(InstanceOfAssertFactories.type(CategoryDTO.class)) //
				.hasFieldOrPropertyWithValue("friendlyName", "/categories/home-appliance");
		assertThat(dto).asInstanceOf(InstanceOfAssertFactories.type(CategoryDTO.class)) //
				.extracting(CategoryDTO::getCoverImage, CategoryDTO::getThumbnail) //
				.containsExactly( //
						new MediaDTO("home-appliance", "image/png", category.getImageUri()), //
						new MediaDTO("home-appliance", "image/gif", category.getThumbnailUri()));
		assertThat(dto.getMetaTags()).hasSizeGreaterThanOrEqualTo(2);
		AssertFactory<MetaTag, ObjectAssert<MetaTag>> metaTagAssertFactory = metaTag -> new ObjectAssert<>(metaTag);
		assertThat(dto.getMetaTags(), metaTagAssertFactory) //
				.contains(new MetaTag(null, category.getMetaKeywords(), null, "keywords"), atIndex(1));
	}

	@Test
	void testFindAllCategories() {
		when(this.categoryRepository.findAll()).thenReturn(categories());
		var categories = this.categoryService.findCategories();
		assertThat(categories).hasSize(3);
	}

	@Test
	void testFindCategoryByDescription() {
		when(this.categoryRepository.findByDescription(anyString())).thenReturn(categories());
		var categories = this.categoryService.findCategoriesByDescription("description");
		assertThat(categories).hasSize(3);
		assertThat(categories).anyMatch(category -> //
		new MediaDTO("category-three", "image/jpg", URI.create("/files/images/category3.jpeg"))
				.equals(category.getCoverImage()));
	}

	@Test
	void testFindCategoryByName() {
		when(this.categoryRepository.findByNameContains(anyString())).thenReturn(categories());
		var categories = this.categoryService.findCategoriesByName("one");
		assertThat(categories).hasSize(3);
	}

	static class Configuration {
		@Bean
		public static MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
			MethodValidationPostProcessor processor = new MethodValidationPostProcessor();
			processor.setValidator(validator);
			return processor;
		}
	}
}
