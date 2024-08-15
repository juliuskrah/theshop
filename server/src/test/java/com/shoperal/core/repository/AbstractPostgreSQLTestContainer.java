package com.shoperal.core.repository;

import jakarta.transaction.Transactional;

import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.TestConstructor.AutowireMode;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest(properties = {
    "spring.datasource.username=testuser", //
    "spring.datasource.password=password", //
    "spring.datasource.driver-class-name=org.postgresql.Driver" //
})
@Transactional
@DirtiesContext
@Testcontainers
@TestConstructor(autowireMode = AutowireMode.ALL)
@AutoConfigureTestEntityManager
public abstract class AbstractPostgreSQLTestContainer {
    @Container
    private static final GenericContainer<?> postgreSQLContainer = new GenericContainer<>("postgres:12-alpine") //
            .withExposedPorts(5432) //
            .withEnv("POSTGRES_PASSWORD", "password") //
            .withEnv("POSTGRES_USER", "testuser") //
            .withEnv("POSTGRES_DB", "testdb") //
            .waitingFor(Wait.forListeningPort());

    @DynamicPropertySource
    static void dynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.hikari.jdbc-url", () -> {
            return String.format("jdbc:postgresql://%s:%d/testdb", //
            postgreSQLContainer.getContainerIpAddress(), //
            postgreSQLContainer.getFirstMappedPort());
        });
    }
}
