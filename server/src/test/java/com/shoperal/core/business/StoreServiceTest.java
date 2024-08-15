package com.shoperal.core.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

import com.shoperal.core.model.CurrencyCode;
import com.shoperal.core.projection.Store;
import com.shoperal.core.repository.MenuRepository;
import com.shoperal.core.repository.StorePreferenceRepository;
import com.shoperal.core.repository.StoreSettingRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class StoreServiceTest {
    @Mock
    private StoreSettingRepository storeRepository;
    @Mock
    private StorePreferenceRepository preferenceRepository;
    @Mock
    private MenuRepository menuRepository;
    @InjectMocks
    private StoreServiceImpl storeService;

    @Test
    void testFindStoreInfo() {
        var store = new Store(){
            @Override
            public CurrencyCode getCurrencyCode() {
                return CurrencyCode.GHS;
            }

            @Override
            public String getId() {
                return "387c8ba7-fc8f-44a9-ac62-a05190ca5be0";
            }

            @Override
            public String getName() {
                return "Shop Admin";
            }

            @Override
            public String getShoperalDomain() {
                return "shop-admin.shoperal.app";
            }

            @Override
            public String getDescription() {
                return "";
            }
        };

        when(storeRepository.findById(any(UUID.class), any())).thenReturn(Optional.of(store));
        var storeDTO = storeService.findStoreInfo(UUID.fromString("387c8ba7-fc8f-44a9-ac62-a05190ca5be0"));
        assertThat(storeDTO).isNotNull() //
            .hasFieldOrPropertyWithValue("currencyCode", CurrencyCode.GHS) //
            .hasFieldOrPropertyWithValue("url", "https://shop-admin.shoperal.app");
    }

    @Test
    void testFindTitle() {
        when(preferenceRepository.findTitle(any(UUID.class))).thenReturn("Test Title");
        storeService.findHomePageTitle(UUID.fromString("9cb585ac-8d24-4702-b354-5fc93da70f99"));
        verify(preferenceRepository, times(1)).findTitle(UUID.fromString("9cb585ac-8d24-4702-b354-5fc93da70f99"));
        verifyNoMoreInteractions(preferenceRepository);
    }

    @Test
    void testFetchAllMenus() {
        storeService.findAllMenus();
        verify(menuRepository, times(1)).findAll(Sort.by("title"));
    }

    @Test
    void testFetchMenuItems() {
        storeService.findMenusItems("footer");
        verify(menuRepository, times(1)).findMenuItem("footer", Sort.by("position"));
    }
}
