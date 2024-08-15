package com.shoperal.core.business;

import java.util.Set;
import java.util.UUID;

import com.shoperal.core.dto.MenuDTO;
import com.shoperal.core.dto.MenuItemDTO;
import com.shoperal.core.dto.StoreDTO;
import com.shoperal.core.model.Menu;
import com.shoperal.core.model.MenuItem;
import com.shoperal.core.projection.Store;
import com.shoperal.core.repository.MenuRepository;
import com.shoperal.core.repository.StorePreferenceRepository;
import com.shoperal.core.repository.StoreSettingRepository;
import com.shoperal.core.utility.StreamUtilities;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

/**
 * @author Julius Krah'
 * @since 1.0
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final StoreSettingRepository storeRepository;
    private final StorePreferenceRepository prefenceRepository;
    private final MenuRepository menuRepository;

    @Builder
	private StoreDTO fromStore(Store store) {
        var dto = new StoreDTO();
        dto.setId(UUID.fromString(store.getId()));
        dto.setName(store.getName());
        dto.setCurrencyCode(store.getCurrencyCode());
        dto.setDescription(store.getDescription());
        UriComponents uriComponents = UriComponentsBuilder.newInstance() //
            .host(store.getShoperalDomain()) //
            .scheme("https").build();
        dto.setUrl(uriComponents.toUriString());
        return dto;
    }

    private MenuDTO transformer(Menu entity) {
        var menu = new MenuDTO();
        menu.setId(entity.getId());
        menu.setTitle(entity.getTitle());
        menu.setHandle(entity.getHandle());
        return menu;
    }

    private MenuItemDTO transformer(MenuItem entity) {
        var menu = new MenuItemDTO();
        menu.setId(entity.getId());
        menu.setTitle(entity.getTitle());
        menu.setPosition(entity.getPosition());
        menu.setUrl(entity.getUri());
        return menu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StoreDTO findStoreInfo(UUID id) {
        return storeRepository.findById(id, Store.class).map(this::fromStore) //
            .orElseThrow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String findHomePageTitle(UUID id) {
        return prefenceRepository.findTitle(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MenuDTO> findAllMenus() {
        var menus = menuRepository.findAll(Sort.by("title"));
        return StreamUtilities.streamSet(menus, this::transformer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<MenuItemDTO> findMenusItems(String handle) {
        var menus = menuRepository.findMenuItem(handle, Sort.by("position"));
        return StreamUtilities.streamSet(menus, this::transformer);
    }
    
}
