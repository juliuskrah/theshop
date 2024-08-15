package com.shoperal.core.repository;

import java.util.Optional;
import java.util.UUID;

import com.shoperal.core.model.StoreSetting;

/**
 * @author Julius Krah
 */
public interface StoreSettingRepository extends BaseRepository<StoreSetting, UUID> {
    <T> Optional<T> findById(UUID id, Class<T> clazz);
}
