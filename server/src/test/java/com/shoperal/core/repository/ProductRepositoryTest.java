package com.shoperal.core.repository;

import static com.shoperal.core.utility.Specifications.productNameGreaterThan;
import static com.shoperal.core.utility.Specifications.productNameLessThan;
import static com.shoperal.core.utility.Specifications.projectProductNameGreaterThan;
import static com.shoperal.core.utility.Specifications.projectProductNameLessThan;
import static org.assertj.core.api.Assertions.as;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.OFFSET_DATE_TIME;
import static org.assertj.core.api.InstanceOfAssertFactories.OPTIONAL;
import static org.springframework.data.jpa.domain.Specification.where;
import static org.springframework.jdbc.datasource.init.ScriptUtils.EOF_STATEMENT_SEPARATOR;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.assertj.core.api.AssertFactory;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.ObjectAssert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.ScrollPosition;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.domain.Window;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.shoperal.core.model.Product;
import com.shoperal.core.model.ProductDetail;
import com.shoperal.core.model.ProductStatus;
import com.shoperal.core.projection.AdminProduct;
import com.shoperal.core.projection.AdminProductDto;
import com.shoperal.core.projection.StoreAdminProduct;
import com.shoperal.core.projection.StoreFrontProduct;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Sql(scripts = { "classpath:scripts/product_data.sql", "classpath:scripts/product_detail_data.sql" }, //
                config = @SqlConfig( //
                                separator = EOF_STATEMENT_SEPARATOR))
@Sql(statements = "DELETE FROM product", executionPhase = AFTER_TEST_METHOD)
public class ProductRepositoryTest extends AbstractPostgreSQLTestContainer {
        private final ProductRepository repository;
        private final ProductDetailRepository detailsRepository;
        private final TestEntityManager em;

        @Test
        void testFindByProductStatus() {
                this.em.getEntityManager().createQuery("DELETE FROM Product p").executeUpdate();
                var product = new Product();
                product.setName("gas cooker");
                product.setFeaturedMedia(URI.create("/files/products/gas-cooker.gif"));
                product.setFriendlyUriFragment(URI.create("gas-cooker"));
                product.setType("downloadable");
                product.setVendor("Binatone");
                product.setStatus(ProductStatus.ACTIVE);
                product.setTags(List.of("cooking", "food"));

                this.em.persist(product);

                var products = this.repository.findByStatus(ProductStatus.ACTIVE);
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(1);
                assertThat(products).element(0).extracting(Product::getTags).isEqualTo(List.of("cooking", "food"));
        }

        @Test
        void testFindByNameContains() {
                var products = this.repository.findByNameContains("orda");
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(1);
                assertThat(products).element(0).extracting(Product::getFeaturedMedia) //
                                .isEqualTo(URI.create("/files/products/jordans.jpeg"));
        }

        @Test
        @WithMockUser("test user")
        void testFindByDescription() {
                var description = "This is a description for product with id = 554c8d4b7444405eb1cac56dbcbf7c9c";
                var product = this.repository.findById(UUID.fromString("554c8d4b-7444-405e-b1ca-c56dbcbf7c9c"));
                assertThat(product).isPresent();
                product.get().setDescription(description);

                var products = this.repository.findByDescription("product with id = 554c8d4");
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(1);
                assertThat(products).element(0).extracting(Product::getFeaturedMedia) //
                                .isEqualTo(URI.create("/files/products/woolen-carpets.jpeg"));
        }

        @Test
        void testFindByProductHandle() {
                var product = this.repository.findByFriendlyUriFragment(URI.create("hoodie"));
                assertThat(product).isPresent();
                assertThat(product).get().extracting("createdDate", as(OPTIONAL))
                                .get(OFFSET_DATE_TIME)
                        .isEqualToIgnoringHours(OffsetDateTime.parse("2020-12-21T21:28:00+01:00"));
                assertThat(product).get().hasFieldOrPropertyWithValue("seo", Collections.emptySet());
        }

        @Test
        void testFindAllProjection() {
                Iterable<StoreFrontProduct> products = this.repository.findAllBy(StoreFrontProduct.class);
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(8);
                assertThat(products).anyMatch(result -> "Woolen Carpets".equals(result.getName()));
        }

        @Test
        void testFindProductByStatusProjection() {
                Iterable<StoreFrontProduct> products = this.repository.findByStatus(ProductStatus.ARCHIVED,
                                StoreFrontProduct.class);
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(3);
        }

        @Test
        void testFindProductContainingNameProjection() {
                Iterable<StoreFrontProduct> products = this.repository.findByNameContains("len Ca",
                                StoreFrontProduct.class);
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(1);
                assertThat(products).element(0).hasFieldOrPropertyWithValue("featuredMedia",
                                "/files/products/woolen-carpets.jpeg");
        }

        @Test
        void testFindProductContainingDescription() {
                Iterable<StoreFrontProduct> products = this.repository
                                .findByDescriptionContains("will not find anything", StoreFrontProduct.class);
                assertThat(products).isEmpty();
        }

        @Test
        @DisplayName("when products are sorted by name asc before 'Jordans'")
        void testFindProductsByNameAscBefore() {
                assertThat(this.repository.count()).isEqualTo(8L);
                Iterable<AdminProduct> products = this.repository.findByNameLessThanAsc("Jordans",
                                UUID.fromString("1fb9e691-033d-4092-b326-99088d401ec9"), 2);
                AssertFactory<AdminProduct, ObjectAssert<AdminProduct>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(2);
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Iphone 11")
                                .hasFieldOrPropertyWithValue("id", "69ca904a-9ec2-4a16-b968-a0a2caa12eec")
                                .hasFieldOrPropertyWithValue("status", ProductStatus.ARCHIVED);
                assertThat(products, productAssertFactory).first() //
                                .satisfies(product -> {
                                        assertThat(product.getName()).isEqualTo("Infinix Note 5");
                                        assertThat(product.getFeaturedMedia()).isEqualTo(
                                                        "/files/products/da03dc88-a354-43b5-a5e3-1c670a7eecb8/infinix-note-5.png");
                                });
        }

        @Test
        @DisplayName("when products are sorted by name asc before 'Jordans' [Window]")
        void testFindProductsByNameAscBeforeWindowed() {
                assertThat(this.repository.count()).isEqualTo(8L);
                Map<String, Object> keys = Map.of(
                                "name", "Jordans",
                                "id", UUID.fromString("1fb9e691-033d-4092-b326-99088d401ec9"));
                var orders = List.of(Order.asc("name").nullsFirst(), //
                                Order.asc("id").nullsFirst());
                var sort = Sort.by(orders);
                var scrollPosition = ScrollPosition.of(keys, ScrollPosition.Direction.BACKWARD);
                Window<AdminProduct> products = this.repository.findBy(this::toPredicate,
                                q -> q.limit(2).as(AdminProduct.class).sortBy(sort).scroll(scrollPosition));
                AssertFactory<AdminProduct, ObjectAssert<AdminProduct>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(2);
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Iphone 11")
                                .hasFieldOrPropertyWithValue("id", "69ca904a-9ec2-4a16-b968-a0a2caa12eec")
                                .hasFieldOrPropertyWithValue("status", ProductStatus.ARCHIVED);
                assertThat(products, productAssertFactory).first() //
                                .satisfies(product -> {
                                        assertThat(product.getName()).isEqualTo("Infinix Note 5");
                                        assertThat(product.getFeaturedMedia()).isEqualTo(
                                                        "/files/products/da03dc88-a354-43b5-a5e3-1c670a7eecb8/infinix-note-5.png");
                                });
        }

        @Test
        @DisplayName("when products are sorted by name desc before 'Iphone 11'")
        void testFindProductsByNameDescBefore() {
                assertThat(this.repository.count()).isEqualTo(8L);
                Iterable<AdminProduct> products = this.repository.findByNameGreaterThanDesc("Iphone 11",
                                UUID.fromString("69ca904a-9ec2-4a16-b968-a0a2caa12eec"), 2);
                AssertFactory<AdminProduct, ObjectAssert<AdminProduct>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(2);
                assertThat(products, productAssertFactory).first() //
                                .satisfies(product -> {
                                        assertThat(product.getName()).isEqualTo("Samsung Galaxy A");
                                        assertThat(product.getId()).isEqualTo("f11d11c5-c8ba-4475-bdf5-d16ffb9d22d4");
                                }).hasFieldOrPropertyWithValue("featuredMedia",
                                                "/files/products/f11d11c5-c8ba-4475-bdf5-d16ffb9d22d4/samsung-galaxy-a.jpg");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Jordans")
                                .hasFieldOrPropertyWithValue("status", ProductStatus.ARCHIVED);
        }

        @Test
        @DisplayName("when products are sorted by name desc before 'Iphone 11' [Window]")
        void testFindProductsByNameDescBeforeWindowed() {
                assertThat(this.repository.count()).isEqualTo(8L);
                Map<String, Object> keys = Map.of(
                                "name", "Iphone 11",
                                "id", UUID.fromString("69ca904a-9ec2-4a16-b968-a0a2caa12eec"));
                var orders = List.of(Order.desc("name").nullsFirst(), //
                                Order.desc("id").nullsFirst());
                var sort = Sort.by(orders);
                var scrollPosition = ScrollPosition.of(keys, ScrollPosition.Direction.BACKWARD);
                Window<AdminProduct> products = this.repository.findBy(this::toPredicate,
                                q -> q.limit(2).sortBy(sort).as(AdminProduct.class).scroll(scrollPosition));
                AssertFactory<AdminProduct, ObjectAssert<AdminProduct>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(2);
                assertThat(products, productAssertFactory).first() //
                                .satisfies(product -> {
                                        assertThat(product.getName()).isEqualTo("Samsung Galaxy A");
                                        assertThat(product.getId()).isEqualTo("f11d11c5-c8ba-4475-bdf5-d16ffb9d22d4");
                                }).hasFieldOrPropertyWithValue("featuredMedia",
                                                "/files/products/f11d11c5-c8ba-4475-bdf5-d16ffb9d22d4/samsung-galaxy-a.jpg");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Jordans")
                                .hasFieldOrPropertyWithValue("status", ProductStatus.ARCHIVED);
        }

        @Test
        @DisplayName("when products are sorted by name asc after 'Iphone 11'")
        void testFindProductsByNameAsc() {
                Sort sort = Sort.by("name", "id");
                var predicates = Map.of( //
                                "id", "69ca904a-9ec2-4a16-b968-a0a2caa12eec", //
                                "name", "Iphone 11");
                assertThat(this.repository.count()).isEqualTo(8L);
                List<Product> products = this.repository.findAll(where(productNameGreaterThan(predicates)), 4, sort);
                AssertFactory<Product, ObjectAssert<Product>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(4);
                assertThat(products, productAssertFactory).element(1) //
                                .hasFieldOrPropertyWithValue("name", "Samsung Galaxy A");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Woolen Carpets");
        }

        @Test
        @DisplayName("when products are sorted by name asc after 'Iphone 11' [Window]")
        void testFindProductsByNameAscWindowed() {
                Map<String, Object> keys = Map.of(
                                "name", "Iphone 11",
                                "id", UUID.fromString("69ca904a-9ec2-4a16-b968-a0a2caa12eec"));
                var orders = List.of(Order.asc("name").nullsFirst(), //
                                Order.asc("id").nullsFirst());
                var sort = Sort.by(orders);
                var scrollPosition = ScrollPosition.of(keys, ScrollPosition.Direction.FORWARD);
                assertThat(this.repository.count()).isEqualTo(8L);
                Window<Product> products = this.repository.findBy(this::toPredicate,
                                q -> q.limit(4).sortBy(sort).scroll(scrollPosition));
                AssertFactory<Product, ObjectAssert<Product>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(4);
                assertThat(products, productAssertFactory).element(1) //
                                .hasFieldOrPropertyWithValue("name", "Samsung Galaxy A");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Woolen Carpets");
        }

        @Test
        @DisplayName("when products are sorted by name desc after 'Jordans'")
        void testFindProductsByNameDesc() {
                Sort sort = Sort.by("name", "id").descending();
                var predicates = Map.of( //
                                "id", "1fb9e691-033d-4092-b326-99088d401ec9", //
                                "name", "Jordans");
                assertThat(this.repository.count()).isEqualTo(8L);
                List<Product> products = this.repository.findAll(where(productNameLessThan(predicates)), 4, sort);
                AssertFactory<Product, ObjectAssert<Product>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(4);
                assertThat(products, productAssertFactory).element(1) //
                                .hasFieldOrPropertyWithValue("name", "Infinix Note 5");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "HTC Desire 12");
        }

        @Test
        @DisplayName("when products are sorted by name desc after 'Jordans'[Windowed]")
        void testFindProductsByNameDescWindowed() {
                Map<String, Object> keys = Map.of(
                                "name", "Jordans",
                                "id", UUID.fromString("1fb9e691-033d-4092-b326-99088d401ec9"));
                var orders = List.of(Order.desc("name").nullsFirst(), //
                                Order.desc("id").nullsFirst());
                var sort = Sort.by(orders);
                var scrollPosition = ScrollPosition.of(keys, ScrollPosition.Direction.FORWARD);
                assertThat(this.repository.count()).isEqualTo(8L);
                Window<Product> products = this.repository.findBy(this::toPredicate,
                                q -> q.limit(4).sortBy(sort).scroll(scrollPosition));
                AssertFactory<Product, ObjectAssert<Product>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(4);
                assertThat(products, productAssertFactory).element(1) //
                                .hasFieldOrPropertyWithValue("name", "Infinix Note 5");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "HTC Desire 12");
        }

        @Test
        @DisplayName("when products are sorted name asc")
        void testFindProductsOrderedByName() {
                var orders = List.of(Order.asc("name").nullsFirst(), //
                                Order.asc("id").nullsFirst());
                Sort sort = Sort.by(orders);
                assertThat(this.repository.count()).isEqualTo(8L);
                Iterable<Product> products = this.repository.findAll(3, sort);
                AssertFactory<Product, ObjectAssert<Product>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(3);
                assertThat(products, productAssertFactory).first() //
                                .hasFieldOrPropertyWithValue("name", "HTC Desire 12");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Infinix Note 5");
        }

        @Test
        @DisplayName("when products are sorted by name asc[Window]")
        void testFindProductsOrderedByNameWindowed() {
                var orders = List.of(Order.asc("name").nullsFirst(), //
                                Order.asc("id").nullsFirst());
                var sort = Sort.by(orders);
                assertThat(this.repository.count()).isEqualTo(8L);
                var scroll = ScrollPosition.keyset();
                Specification<Product> spec = this::toPredicate;
                Window<Product> products = this.repository.findBy(spec, q -> q.limit(3).sortBy(sort).scroll(scroll));
                AssertFactory<Product, ObjectAssert<Product>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(3);
                assertThat(products, productAssertFactory).first() //
                                .hasFieldOrPropertyWithValue("name", "HTC Desire 12");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Infinix Note 5");
        }

        @Test
        @DisplayName("when products are sorted name desc")
        void testFindProductsOrderedByNameDesc() {
                var orders = List.of(Order.desc("name").nullsLast(), //
                                Order.desc("id").nullsLast());
                Sort sort = Sort.by(orders);
                assertThat(this.repository.count()).isEqualTo(8L);
                Iterable<Product> products = this.repository.findAll(3, sort);
                AssertFactory<Product, ObjectAssert<Product>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(3);
                assertThat(products, productAssertFactory).first() //
                                .hasFieldOrPropertyWithValue("name", "Woolen Carpets");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Samsung Galaxy A");
        }

        @Test
        @DisplayName("when products are sorted name desc[Window]")
        void testFindProductsOrderedByNameDescWindowed() {
                var orders = List.of(Order.desc("name").nullsFirst(), //
                                Order.desc("id").nullsFirst());
                var sort = Sort.by(orders);
                assertThat(this.repository.count()).isEqualTo(8L);
                var scroll = ScrollPosition.keyset();
                Window<Product> products = this.repository.findBy(this::toPredicate,
                                q -> q.limit(3).sortBy(sort).scroll(scroll));
                AssertFactory<Product, ObjectAssert<Product>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(3);
                assertThat(products, productAssertFactory).first() //
                                .hasFieldOrPropertyWithValue("name", "Woolen Carpets");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Samsung Galaxy A");
        }

        @Test
        @DisplayName("when projection products are sorted by name desc after 'Jordans'")
        void testProjectedProductsByNameDesc() {
                Sort sort = Sort.by("name", "id").descending();
                var predicates = Map.of( //
                                "id", "1fb9e691-033d-4092-b326-99088d401ec9", //
                                "name", "Jordans");
                assertThat(this.repository.count()).isEqualTo(8L);
                List<StoreFrontProduct> products = this.repository.findAll(
                                where(projectProductNameLessThan(predicates)), 4, sort, StoreFrontProduct.class);
                AssertFactory<StoreFrontProduct, ObjectAssert<StoreFrontProduct>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(4);
                assertThat(products, productAssertFactory).first() //
                                .hasFieldOrPropertyWithValue("name", "Iphone 11") //
                                .hasFieldOrPropertyWithValue("status", ProductStatus.ARCHIVED);
                assertThat(products, productAssertFactory).element(1) //
                                .hasFieldOrPropertyWithValue("name", "Infinix Note 5");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "HTC Desire 12");
        }

        @Test
        @DisplayName("when projection products are sorted by name asc after 'Iphone 11'")
        void testProjectedProductsByNameAsc() {
                Sort sort = Sort.by("name", "id");
                var predicates = Map.of( //
                                "id", "69ca904a-9ec2-4a16-b968-a0a2caa12eec", //
                                "name", "Iphone 11");
                assertThat(this.repository.count()).isEqualTo(8L);
                List<AdminProduct> products = this.repository.findAll(where(projectProductNameGreaterThan(predicates)),
                                4, sort, AdminProduct.class);
                AssertFactory<AdminProduct, ObjectAssert<AdminProduct>> productAssertFactory = this::assertFactory;
                assertThat(products).isNotEmpty();
                assertThat(products).hasSize(4);
                assertThat(products, productAssertFactory).element(1) //
                                .hasFieldOrPropertyWithValue("name", "Samsung Galaxy A") //
                                .hasFieldOrPropertyWithValue("id", "f11d11c5-c8ba-4475-bdf5-d16ffb9d22d4");
                assertThat(products, productAssertFactory).last() //
                                .hasFieldOrPropertyWithValue("name", "Woolen Carpets");
        }

        @Test
        void fetchProductWithDetailsTest() {
                var productDetail = detailsRepository.findById(UUID.fromString("f11d11c5-c8ba-4475-bdf5-d16ffb9d22d4"));
                assertThat(productDetail).isPresent();
                assertThat(productDetail).get(InstanceOfAssertFactories.type(ProductDetail.class)) //
                                .hasFieldOrPropertyWithValue("costPrice", 800.00)
                                .extracting(ProductDetail::getProduct)
                                .hasFieldOrPropertyWithValue("friendlyUriFragment", URI.create("samsung-galaxy-a"));
        }

        @Test
        @Disabled
        void fetchProjectedProductWithDetailsTest() {
                var storeFrontProduct = detailsRepository.findById( //
                                UUID.fromString("f11d11c5-c8ba-4475-bdf5-d16ffb9d22d4"), StoreAdminProduct.class);
                assertThat(storeFrontProduct).isPresent() //
                                .get(InstanceOfAssertFactories.type(StoreAdminProduct.class)) //
                                .hasFieldOrPropertyWithValue("costPrice", 800.00)
                                .extracting(StoreAdminProduct::getProduct).isNotNull()
                                .hasFieldOrPropertyWithValue("friendlyUriFragment", URI.create("samsung-galaxy-a"));
        }

        @Test
        void fetchProductWithDetailsDTOTest() {
                var storeFrontProduct = detailsRepository
                                .findOne(UUID.fromString("f11d11c5-c8ba-4475-bdf5-d16ffb9d22d4"));
                assertThat(storeFrontProduct).isPresent() //
                                .get(InstanceOfAssertFactories.type(AdminProductDto.class)) //
                                .hasFieldOrPropertyWithValue("costPrice", 800.00) //
                                .hasFieldOrPropertyWithValue("name", "Samsung Galaxy A") //
                                .hasFieldOrPropertyWithValue("status", ProductStatus.ARCHIVED) //
                                .hasFieldOrPropertyWithValue("featuredMedia", URI.create(
                                                "/files/products/f11d11c5-c8ba-4475-bdf5-d16ffb9d22d4/samsung-galaxy-a.jpg"))
                                .hasFieldOrPropertyWithValue("friendlyUriFragment", URI.create("samsung-galaxy-a")) //
                                .hasFieldOrPropertyWithValue("tags", List.of("phone", "samsung", "galaxy"));
        }

        private ObjectAssert<Product> assertFactory(Product product) {
                return new ObjectAssert<>(product);
        }

        private <T extends AdminProduct> ObjectAssert<T> assertFactory(T product) {
                return new ObjectAssert<>(product);
        }

        private <T> Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                return null;
        }

}