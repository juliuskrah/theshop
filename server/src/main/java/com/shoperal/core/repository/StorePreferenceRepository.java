package com.shoperal.core.repository;

import java.util.UUID;

import com.shoperal.core.model.StorePreference;

import org.springframework.data.jpa.repository.Query;

/**
 * @author Julius Krah
 */
public interface StorePreferenceRepository extends BaseRepository<StorePreference, UUID> {
    @Query("SELECT sp.homePageTitle FROM StorePreference sp WHERE sp.id = :id")
    String findTitle(UUID id);
}
