package com.shoperal.core.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoperal.core.model.ProductStatus;
import com.shoperal.core.projection.AdminProduct;
import com.shoperal.core.repository.ProductRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.data.domain.Sort;

/**
 * @author Julius Krah
 */
@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class ProductServiceTest {
    @Mock(lenient = true)
    private ProductRepository productRepository;
    @Mock
    private ObjectMapper mapper;
    @InjectMocks
    private ProductServiceImpl productService;
    private static List<AdminProduct> products;

    static {
        products = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            var product = new AdminProduct() {
                UUID rand = UUID.randomUUID();

                @Override
                public String getId() {
                    return rand.toString();
                }

                @Override
                public String getName() {
                    return "product" + rand;
                }

                @Override
                public ProductStatus getStatus() {
                    return ProductStatus.ACTIVE;
                }

                @Override
                public String getFeaturedMedia() {
                    return "/" + rand;
                }

                @Override
                public String getType() {
                    return "Phone";
                }

                @Override
                public String getVendor() {
                    return "Store" + rand;
                }

            };
            products.add(product);
        }
    }

    @Test
    void whenListDefaultPaginationHasNext() throws JsonProcessingException {
        ongoingStubbing();
        when(mapper.writeValueAsString(any())).thenReturn("{}");
        var paginationWrapper = productService.findProducts(Map.of(), 5, Sort.by("name", "id"));
        assertThat(paginationWrapper).isNotNull();
        assertThat(paginationWrapper.getContent()).hasSize(5);
        assertThat(paginationWrapper.isHasNext()).isTrue();
        assertThat(paginationWrapper.isHasPrevious()).isFalse();
        assertThat(paginationWrapper.getAfter()).isNotNull();
        assertThat(paginationWrapper.getBefore()).isNull();
    }

    @Test
    void whenListDefaultPagination() throws JsonProcessingException {
        ongoingStubbing();
        var paginationWrapper = productService.findProducts(Map.of(), 10, Sort.by("name", "id").descending());
        assertThat(paginationWrapper).isNotNull();
        assertThat(paginationWrapper.getContent()).hasSize(10);
        assertThat(paginationWrapper.isHasNext()).isFalse();
        assertThat(paginationWrapper.isHasPrevious()).isFalse();
        assertThat(paginationWrapper.getAfter()).isNull();
        assertThat(paginationWrapper.getBefore()).isNull();
    }

    private OngoingStubbing<List<AdminProduct>> ongoingStubbing() {
        return when(productRepository.findAll(isNull(), anyInt(), any(Sort.class),
                ArgumentMatchers.<Class<AdminProduct>>any())) //
                        .thenAnswer((invocation) -> {
                            var products = new ArrayList<>(ProductServiceTest.products);
                            int maxResult = invocation.getArgument(1);
                            if (maxResult < 10) {
                                return products.subList(0, maxResult);
                            }
                            return products;
                        });
    }
}
