package com.shoperal.core.business;

import static com.shoperal.core.utility.Specifications.projectProductNameGreaterThan;
import static com.shoperal.core.utility.Specifications.projectProductNameLessThan;
import static org.springframework.data.jpa.domain.Specification.where;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shoperal.core.dto.ProductDTO;
import com.shoperal.core.model.Product;
import com.shoperal.core.projection.AdminProduct;
import com.shoperal.core.projection.StoreFrontProduct;
import com.shoperal.core.repository.ProductRepository;
import com.shoperal.core.utility.PaginationHelper;
import com.shoperal.core.utility.StreamUtilities;

import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import groovy.transform.builder.Builder;
import lombok.RequiredArgsConstructor;

/**
 * @author Julius Krah
 * @since 1.0.0
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final ObjectMapper mapper;

    private ProductDTO transformer(AdminProduct projection) {
        var product = new ProductDTO();
        product.setId(UUID.fromString(projection.getId()));
        product.setName(projection.getName());
        product.setStatus(projection.getStatus().name());
        product.setMediaSrc(projection.getFeaturedMedia());
        product.setType(projection.getType());
        product.setVendor(projection.getVendor());
        return product;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private PaginationWrapper<ProductDTO> pageByName(Map<String, String> options, int maxResults, Sort sort) {
        var pagination = new PaginationWrapper<ProductDTO>();
        var helper = new PaginationHelper(mapper);
        var nameOrder = sort.stream().filter(order -> "name".equals(order.getProperty())).findAny();
        if (options.containsKey("before")) {
            nameOrder.ifPresent(consumer -> {
                Assert.isTrue(options.containsKey("name"), "'name' must be present");
                Assert.isTrue(options.containsKey("id"), "'id' must be present");
                var name = options.get("name");
                var id = UUID.fromString(options.get("id"));
                List<AdminProduct> products = new ArrayList<>();
                if (consumer.isAscending()) {
                    products = (List) this.productRepository.findByNameLessThanAsc(name, id, maxResults + 1);
                } else if (consumer.isDescending()) {
                    products = (List) this.productRepository.findByNameGreaterThanDesc(name, id, maxResults + 1);
                }
                if (products.size() > maxResults) {
                    pagination.setHasPrevious(true);
                    // remove first item
                    products.remove(0);
                    var firstProduct = products.get(0);
                    var before = helper.toBeforeOrAfterToken(Map.of( //
                            "id", firstProduct.getId().toString(), //
                            "field", "name", //
                            "value", firstProduct.getName() //
                    ));
                    pagination.setBefore(before);
                }
                pagination.setHasNext(true);
                var lastProduct = products.get(products.size() - 1);
                var after = helper.toBeforeOrAfterToken(Map.of( //
                        "id", lastProduct.getId().toString(), //
                        "field", "name", //
                        "value", lastProduct.getName() //
                ));
                pagination.setAfter(after);
                var dtos = StreamUtilities.stream(products, this::transformer);
                pagination.setContent(dtos);
            });
        } else if (options.containsKey("after")) {
            nameOrder.ifPresent(consumer -> {
                List<AdminProduct> products = new ArrayList<>();
                if (consumer.isAscending()) {
                    // If we get maxResults + 1 items, theres a next page
                    products = this.productRepository.findAll(where(projectProductNameGreaterThan(options)),
                            maxResults + 1, sort, AdminProduct.class);
                } else if (consumer.isDescending()) {
                    // If we get maxResults + 1 items, theres a next page
                    products = this.productRepository.findAll(where(projectProductNameLessThan(options)),
                            maxResults + 1, sort, AdminProduct.class);
                }
                if (products.size() > maxResults) {
                    pagination.setHasNext(true);
                    // remove last item
                    products.remove(products.size() - 1);
                    var lastProduct = products.get(products.size() - 1);
                    var after = helper.toBeforeOrAfterToken(Map.of( //
                            "id", lastProduct.getId().toString(), //
                            "field", "name", //
                            "value", lastProduct.getName() //
                    ));
                    pagination.setAfter(after);
                }
                pagination.setHasPrevious(true);
                var firstProduct = products.get(0);
                var before = helper.toBeforeOrAfterToken(Map.of( //
                        "id", firstProduct.getId().toString(), //
                        "field", "name", //
                        "value", firstProduct.getName() //
                ));
                pagination.setBefore(before);
                var dtos = StreamUtilities.stream(products, this::transformer);
                pagination.setContent(dtos);
            });
        }
        return pagination;
    }

    private PaginationWrapper<ProductDTO> pageByDateCreated(Map<String, String> options, int maxResults, Sort sort) {
        return null;
    }

    private PaginationWrapper<ProductDTO> pageByDateModified(Map<String, String> options, int maxResults, Sort sort) {
        return null;
    }

    @Builder
    Product toEntity(ProductDTO product) {
        var entity = new Product();
        entity.setName(product.getName());
        entity.setDescription(product.getDescription());
        if (Objects.nonNull(product.getMediaSrc())) {
            entity.setFeaturedMedia(URI.create(product.getMediaSrc()));
        }
        if (Objects.nonNull(product.getFriendlyName())) {
            entity.setFriendlyUriFragment(URI.create(product.getFriendlyName()));
        }
        return entity;
    }

    @Builder
    ProductDTO fromEntity(Product product) {
        var dto = new ProductDTO();
        return dto;
    }

    @Override
    public List<ProductDTO> findProducts() {
        var products = productRepository.findAllBy(StoreFrontProduct.class);
        return StreamUtilities.stream(products, this::transformer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PaginationWrapper<ProductDTO> findProducts(Map<String, String> options, int maxResults, Sort sort) {
        // Cap max results to 100 items per page
        if (maxResults > 100) {
            maxResults = 100;
        }
        var helper = new PaginationHelper(mapper);
        options = helper.decodeTokens(options);

        if (options.containsKey("name")) {
            return pageByName(options, maxResults, sort);
        } else if (options.containsKey("createdDate")) {
            return pageByDateCreated(options, maxResults, sort);
        } else if (options.containsKey("lastModifiedDate")) {
            return pageByDateModified(options, maxResults, sort);
        } else {
            var pagination = new PaginationWrapper<ProductDTO>();
            // If we get maxResults + 1 items, theres a next page
            var products = this.productRepository.findAll(null, maxResults + 1, sort, AdminProduct.class);
            if (products.size() > maxResults) {
                pagination.setHasNext(true);
                // remove last item
                products.remove(products.size() - 1);
                var lastProduct = products.get(products.size() - 1);
                var after = helper.toBeforeOrAfterToken(Map.of( //
                        "id", lastProduct.getId().toString(), //
                        "field", "name", //
                        "value", lastProduct.getName() //
                ));
                pagination.setAfter(after);
            }
            // This is the first page, there's no before
            var dtos = StreamUtilities.stream(products, this::transformer);
            pagination.setContent(dtos);
            return pagination;
        }
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public ProductDTO createProduct(ProductDTO product) {
        var entity = toEntity(product);
        productRepository.save(entity);
        return fromEntity(entity);
    }

    @Transactional
    @Override
    public ProductDTO modifyProduct(UUID id, ProductDTO product) {
        // TODO Auto-generated method stub
        return null;
    }

    @Transactional
    @Override
    public void removeProduct(UUID id) {
        // TODO Auto-generated method stub
    }

}
