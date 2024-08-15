package com.shoperal.core.repository;

import java.util.UUID;

import com.shoperal.core.model.Menu;
import com.shoperal.core.model.MenuItem;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

public interface MenuRepository extends BaseRepository<Menu, UUID> {
    Iterable<Menu> findAll(Sort sort);
    @Query("SELECT mi FROM MenuItem mi where mi.menu.handle = :handle")
    Iterable<MenuItem> findMenuItem(String handle, Sort sort);
}
