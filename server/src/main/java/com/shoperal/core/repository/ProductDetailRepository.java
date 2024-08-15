package com.shoperal.core.repository;

import java.util.Optional;
import java.util.UUID;

import com.shoperal.core.model.ProductDetail;
import com.shoperal.core.projection.AdminProductDto;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface ProductDetailRepository extends //
        BaseRepository<ProductDetail, UUID>, JpaSpecificationExecutor<ProductDetail> {
    <T> Optional<T> findById(UUID id, Class<T> clazz);

    @Query("SELECT new com.shoperal.core.projection.AdminProductDto(" + //
            "p.id, p.name, pd.type, pd.vendor, pd.costPrice, p.price, p.comparedPrice," + //
            "pd.status, pd.description, p.featuredMedia, p.friendlyUriFragment, 0, pd.tags" + //
            ") FROM ProductDetail pd JOIN pd.product p WHERE p.id = :id")
    Optional<AdminProductDto> findOne(UUID id);
}
