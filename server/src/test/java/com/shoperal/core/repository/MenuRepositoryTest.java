package com.shoperal.core.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;

import com.shoperal.core.config.JpaConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import lombok.RequiredArgsConstructor;

@DataJpaTest
@RequiredArgsConstructor
@Import(JpaConfiguration.class)
@TestConstructor(autowireMode = AutowireMode.ALL)
@Sql(scripts = {"classpath:scripts/menu_data.sql"}, 
config = @SqlConfig(separator = org.springframework.jdbc.datasource.init.ScriptUtils.EOF_STATEMENT_SEPARATOR))
public class MenuRepositoryTest {
    private final MenuRepository menuRepository;

    @Test
    void testFindMenu() {
        var menus = menuRepository.findAll(Sort.by("title"));
        assertThat(menus).hasSize(2) //
            .element(0).hasFieldOrPropertyWithValue("title", "Footer menu");
    }

    @Test
    void testFindMenuItem() {
        var menuItems = menuRepository.findMenuItem("main-menu", Sort.by("position"));
        assertThat(menuItems).hasSize(2).element(0) //
            .hasFieldOrPropertyWithValue("title", "Home") //
            .hasFieldOrPropertyWithValue("uri", URI.create("/"));
    }
}
