package com.shoperal.core.repository;

import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.URI_TYPE;
import static org.springframework.data.domain.Sort.Direction.DESC;

import java.net.URI;
import java.util.UUID;

import com.shoperal.core.config.JpaConfiguration;
import com.shoperal.core.model.Category;
import com.shoperal.core.model.CategoryMenuThumbnail;

import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import lombok.RequiredArgsConstructor;

@DataJpaTest
@TestConstructor(autowireMode = AutowireMode.ALL)
@Import(JpaConfiguration.class)
@RequiredArgsConstructor
@Sql(scripts = "classpath:scripts/category_menu_thumbnail_data.sql", config = @SqlConfig(separator = org.springframework.jdbc.datasource.init.ScriptUtils.EOF_STATEMENT_SEPARATOR))
public class CategoryMenuThumbnailRepositoryTest {
    private final CategoryMenuThumbnailRepository repository;
    private final TestEntityManager em;

    @Test
    void testFindByUri() {
        var thumbnail = repository.findByUri(
                URI.create("/thumbnail/categories/569e6f87-905e-43ad-a7b1491378b7482e/laptops-and-desktops-thumb.png"));
        assertThat(thumbnail).isPresent();
        assertThat(thumbnail.get()).extracting("uri", as(URI_TYPE)) //
                .hasPath("/thumbnail/categories/569e6f87-905e-43ad-a7b1491378b7482e/laptops-and-desktops-thumb.png");
        assertThat(thumbnail).get().hasFieldOrPropertyWithValue("mediaType", "image/png");
    }

    @Test
    void testFindByCategory() {
        var category = em.find(Category.class, UUID.fromString("ea9f011a-7292-4881-ba7e-b86b0f2da20f"));
        var thumbnails = repository.findByCategory(category);
        assertThat(thumbnails).hasSize(3);
        AssertFactory<CategoryMenuThumbnail, ObjectAssert<CategoryMenuThumbnail>> thumbnailAssertFactory = (
                thumbnail) -> new ObjectAssert<>(thumbnail);
        assertThat(thumbnails, thumbnailAssertFactory)
                .anyMatch(thumbnail -> "phone-and-accessories-thumb-2".equals(thumbnail.getAlt()));
    }

    @Test
    void testFindByCategorySorted() {
        var category = em.find(Category.class, UUID.fromString("ea9f011a-7292-4881-ba7e-b86b0f2da20f"));
        var thumbnails = repository.findByCategory(category, Sort.by(DESC, "priority"));
        assertThat(thumbnails).hasSize(3);
        AssertFactory<CategoryMenuThumbnail, ObjectAssert<CategoryMenuThumbnail>> thumbnailAssertFactory = (
                thumbnail) -> new ObjectAssert<>(thumbnail);
        assertThat(thumbnails, thumbnailAssertFactory).first().hasFieldOrPropertyWithValue("alt",
                "phone-and-accessories-thumb-3");
    }

    @Test
    void testFindByMediaType() {
        var thumbnails = repository.findByMediaType("image/gif");
        assertThat(thumbnails).hasSize(2);
    }

    @Test
    @DisplayName("test find by category and media-type")
    void testFindByCategoryMediaType() {
        var category = em.find(Category.class, UUID.fromString("ea9f011a-7292-4881-ba7e-b86b0f2da20f"));
        var thumbnails = repository.findByCategoryMediaType(category, "image/gif");
        assertThat(thumbnails).hasSize(2);
    }
}
