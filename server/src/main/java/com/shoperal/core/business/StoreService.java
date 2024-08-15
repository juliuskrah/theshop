package com.shoperal.core.business;

import java.util.Set;
import java.util.UUID;

import com.shoperal.core.dto.MenuDTO;
import com.shoperal.core.dto.MenuItemDTO;
import com.shoperal.core.dto.StoreDTO;

import org.springframework.validation.annotation.Validated;

/**
 * The service class contains methods that handle the business operations and
 * rules for Shoperal Stores.
 * 
 * @implNote As a rule, the following recommendations should be adopted
 *           <ol>
 *           <li>Read operations should be prefixed with <code>find*</code></li>
 *           <li>Write operations should be prefixed with
 *           <code>create*</code></li>
 *           <li>Update operations should be prefixed with
 *           <code>modify*</code></li>
 *           <li>Delete operations should be prefixed with
 *           <code>remove*</code></li>
 *           </ol>
 *           This should make it easy for AOP <code>pointcut expressions</code>
 *           to match
 * @author Julius Krah
 */
@Validated
public interface StoreService {
    /**
     * Return store info by store id. In the current implementation, all stores
     * have the same ID. The StoreSetting table only has one row.
     * This may change in future implementations as we observe
     * the implications of this approach
     * @param id store id. The id is the same for all stores
     * @return store info
     */
    StoreDTO findStoreInfo(UUID id);

    /**
     * Gives to the caller the homepage title as configured by the merchant
     * @param id store id. The id is the same for all stores
     * @return home page title
     */
    String findHomePageTitle(UUID id);

    /**
     * Retrieves all menus sorted by position
     * @return menus
     */
    Set<MenuDTO> findAllMenus();

    /**
     * Retrieves all menu items that match the handle
     * @param handle
     * @return menu items
     */
    Set<MenuItemDTO> findMenusItems(String handle);
}
