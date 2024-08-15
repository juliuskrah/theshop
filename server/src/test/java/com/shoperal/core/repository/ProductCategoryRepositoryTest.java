package com.shoperal.core.repository;

import static com.shoperal.core.utility.Specifications.productInCategory;
import static com.shoperal.core.utility.Specifications.productInCategoryHandle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.data.jpa.domain.Specification.where;

import java.net.URI;
import java.util.UUID;

import com.shoperal.core.config.JpaConfiguration;
import com.shoperal.core.model.Product;
import com.shoperal.core.projection.AdminProduct;
import com.shoperal.core.projection.StoreFrontProduct;

import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import lombok.RequiredArgsConstructor;

@TestConstructor(autowireMode = AutowireMode.ALL)
@RequiredArgsConstructor
@Sql(scripts = { //
        "classpath:scripts/category_data.sql", //
        "classpath:scripts/product_data.sql", //
        "classpath:scripts/product_category_data.sql" }, config = @SqlConfig(separator = org.springframework.jdbc.datasource.init.ScriptUtils.EOF_STATEMENT_SEPARATOR))
@DataJpaTest(includeFilters = { @Filter(type = ASSIGNABLE_TYPE, classes = JpaConfiguration.class) })
public class ProductCategoryRepositoryTest {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Test
    void findProductCountByCategoryTest() {
        assertThat(productRepository.count()).isEqualTo(8);
        var count8 = productRepository.countByCategory(UUID.fromString("bcab97e0-2260-47cc-87d5-4ad418b72553"));
        assertThat(count8).isEqualTo(8);
        var count7 = productRepository.countByCategory(UUID.fromString("a27a3860-0e5d-461f-ab5e-4862ddd81834"));
        assertThat(count7).isEqualTo(7);
        var count6 = productRepository.countByCategory(UUID.fromString("00ab3238-24fc-4e72-a57c-c6a42137a36b"));
        assertThat(count6).isEqualTo(6);
        var count5 = productRepository.countByCategory(UUID.fromString("430f60b1-fa49-42bc-973e-cd49e296a185"));
        assertThat(count5).isEqualTo(5);
    }

    @Test
    void findCategoryCountByProductTest() {
        assertThat(categoryRepository.count()).isEqualTo(5);
        var count1 = categoryRepository.countByProduct(UUID.fromString("1fb9e691-033d-4092-b326-99088d401ec9"));
        assertThat(count1).isEqualTo(1);
        var count3 = categoryRepository.countByProduct(UUID.fromString("05097672-9f41-4a51-ac6c-3c673644cdc0"));
        assertThat(count3).isEqualTo(3);
        var count4 = categoryRepository.countByProduct(UUID.fromString("69ca904a-9ec2-4a16-b968-a0a2caa12eec"));
        assertThat(count4).isEqualTo(4);
    }

    @Test
    void findProductByCategoryTest() {
        var products = productRepository.findByCategory(UUID.fromString("430f60b1-fa49-42bc-973e-cd49e296a185"));
        assertThat(products).hasSize(5);

        assertThat(products, this::productAssert).first() //
                .hasFieldOrPropertyWithValue("name", "HTC Desire 12");
        assertThat(products, this::productAssert).last() //
                .hasFieldOrPropertyWithValue("name", "Samsung Galaxy A");
        assertThat(products, this::productAssert).element(2) //
                .hasFieldOrPropertyWithValue("name", "Iphone 11");
    }

    @Test
    void findDynamicProductProjectionByCategoryTest() {
        var products = productRepository.findAll(where(productInCategory( //
                UUID.fromString("a27a3860-0e5d-461f-ab5e-4862ddd81834") //
        )), 20, Sort.by("name"), StoreFrontProduct.class);
        assertThat(products).hasSize(7);

        assertThat(products, this::productAdminAssert).first() //
                .hasFieldOrPropertyWithValue("name", "HTC Desire 12");
        assertThat(products, this::productAdminAssert).last() //
                .hasFieldOrPropertyWithValue("name", "Woolen Carpets");
        assertThat(products, this::productAdminAssert).element(3) //
                .hasFieldOrPropertyWithValue("name", "Iphone 11");
    }

    @Test
    void findDynamicProductProjectionByCategoryHandleTest() {
        var products = productRepository.findAll(where(productInCategoryHandle( //
                URI.create("blender-00ab3238") //
        )), 20, Sort.by("name"), StoreFrontProduct.class);
        assertThat(products).hasSize(6);

        assertThat(products, this::productAdminAssert).first() //
                .hasFieldOrPropertyWithValue("name", "HTC Desire 12");
        assertThat(products, this::productAdminAssert).last() //
                .hasFieldOrPropertyWithValue("name", "Woolen Carpets");
        assertThat(products, this::productAdminAssert).element(3) //
                .hasFieldOrPropertyWithValue("name", "Iphone 11");
    }

    ObjectAssert<Product> productAssert(Product product) {
        return new ObjectAssert<>(product);
    }

    ObjectAssert<AdminProduct> productAdminAssert(AdminProduct product) {
        return new ObjectAssert<>(product);
    }
}
