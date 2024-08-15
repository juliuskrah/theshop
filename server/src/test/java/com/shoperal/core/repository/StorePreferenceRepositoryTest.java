package com.shoperal.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import com.shoperal.core.config.JpaConfiguration;

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
@Sql(scripts = {"classpath:scripts/store_setting_data.sql", "classpath:scripts/store_preference_data.sql"}, 
config = @SqlConfig(separator = org.springframework.jdbc.datasource.init.ScriptUtils.EOF_STATEMENT_SEPARATOR))
public class StorePreferenceRepositoryTest {
    private final StorePreferenceRepository preferenceRepository;
    
    @Test
    void testFindHomePageTitle() {
        var pageTitle = preferenceRepository.findTitle(UUID.fromString("eeca9d1b-759f-4ac1-aa5e-318acd5a5a47"));
        assertThat(pageTitle).isNotNull().isEqualTo("Fenty Home");
    }
}
