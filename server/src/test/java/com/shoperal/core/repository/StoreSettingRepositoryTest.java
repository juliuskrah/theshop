package com.shoperal.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;

import com.shoperal.core.config.JpaConfiguration;
import com.shoperal.core.model.CurrencyCode;
import com.shoperal.core.model.StoreSetting;
import com.shoperal.core.projection.Store;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import lombok.RequiredArgsConstructor;

/**
 * @author Julius Krah
 */
@DataJpaTest
@RequiredArgsConstructor
@Import(JpaConfiguration.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Sql(scripts = "classpath:scripts/store_setting_data.sql", config = @SqlConfig(separator = org.springframework.jdbc.datasource.init.ScriptUtils.EOF_STATEMENT_SEPARATOR))
public class StoreSettingRepositoryTest {
    private final StoreSettingRepository repository;

    @Test
    void testFindStoreSettingById() {
        var storeSetting = repository.findById(UUID.fromString("86aac602-37d2-47e2-b34a-16e77d48e9c1"));
        assertThat(storeSetting).isPresent();
        assertThat(storeSetting).flatMap(setting -> Optional.of(setting.getAddress())) //
                .hasValueSatisfying(address -> assertThat(address.getId()) //
                        .isEqualTo(UUID.fromString("709f2448-ca57-4895-b086-f6520d015bdb")));
        assertThat(storeSetting).map(StoreSetting::getPresentmentCurrencies) //
                .hasValueSatisfying(currecies -> assertThat(currecies).contains(CurrencyCode.ZMW));
    }

    @Test
    void testFindStoreInfo() {
        var store = repository.findById(UUID.fromString("eeca9d1b-759f-4ac1-aa5e-318acd5a5a47"), Store.class);
        assertThat(store).isPresent();
        assertThat(store).map(Store::getName).hasValue("fenty");
        assertThat(store).map(Store::getShoperalDomain).hasValue("fenty.shoperal.app");
        assertThat(store).map(Store::getDescription).isEmpty();
        assertThat(store).map(Store::getCurrencyCode).hasValue(CurrencyCode.KES);
    }
}
