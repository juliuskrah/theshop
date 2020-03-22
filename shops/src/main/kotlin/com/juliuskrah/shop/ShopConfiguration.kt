package com.juliuskrah.shop

import com.juliuskrah.shop.domain.Tenant
import com.juliuskrah.shop.repository.TenantAwareRoutingConnectionFactory
import com.juliuskrah.shop.security.TenantWebFilter
import io.r2dbc.spi.Connection
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions.*
import io.r2dbc.spi.Option
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryBuilder
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.EnableScheduling
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ApplicationProperties::class)
class ShopConfiguration(
        val r2dbcProperties: R2dbcProperties,
        val applicationProperties: ApplicationProperties
) : ApplicationContextAware {
    private lateinit var applicationContext: ApplicationContext
    private val log = LoggerFactory.getLogger(ShopConfiguration::class.java)

    @Bean
    fun connectionFactory(): ConnectionFactory {
        val connectionFactory = TenantAwareRoutingConnectionFactory()
        val defaultFactory = masterConnectionFactory(r2dbcProperties)
        val tenants = tenants(Mono.from(defaultFactory.create())).collectList().block()
        log.debug("discovered tenants {}", tenants?.size)
        val factories = mutableMapOf<String, ConnectionFactory>()
        for (tenant: Tenant in tenants!!) {
            factories[tenant.name] = tenantConnectionFactory(tenant)
        }
        TenantWebFilter.lookup.connectionFactories = factories
        connectionFactory.setConnectionFactoryLookup(TenantWebFilter.lookup)
        connectionFactory.setTargetConnectionFactories(TenantWebFilter.lookup.connectionFactories)
        connectionFactory.setDefaultTargetConnectionFactory(defaultFactory)
        // Set to 'false' to ensure unknown tenants do not accidentally fetch data for main tenant
        connectionFactory.setLenientFallback(false)
        return connectionFactory
    }

    fun tenants(connectionFactory: Mono<Connection>): Flux<Tenant> {
        return connectionFactory.flatMapMany { connection ->
            connection.createStatement("""
                SELECT id, name, database_host_name, database_port,
                database_username, database_password, database_name
                FROM tenant
            """)
                    .execute()
        }.flatMap { result ->
            result.map { row, _ ->
                val id = row.get("id", UUID::class.java)
                        ?: throw IllegalStateException("id cannot be null")
                val name = row.get("name", String::class.java)
                        ?: throw IllegalStateException("name cannot be null")
                val databasePassword = row.get("database_password", String::class.java)
                        ?: throw IllegalStateException("password cannot be null")
                val databaseHostName = row.get("database_host_name", String::class.java)
                        ?: applicationProperties.databaseHost
                val databasePort = row.get("database_port", Integer::class.java)?.toInt() ?: 5432
                val databaseName = row.get("database_name", String::class.java) ?: name
                val databaseUserName = row.get("database_username", String::class.java) ?: name

                Tenant(id, name, databaseHostName, databasePort, databaseUserName, databasePassword, databaseName)
            }
        }
    }

    fun masterConnectionFactory(r2dbcProperties: R2dbcProperties): ConnectionFactory {
        return ConnectionFactoryBuilder.create(r2dbcProperties).build()
    }

    fun tenantConnectionFactory(tenant: Tenant): ConnectionFactory {
        return ConnectionFactories.get(builder()
                .option(DRIVER, "postgresql")
                .option(HOST, tenant.databaseHostName!!)
                .option(PORT, tenant.databasePort)
                .option(USER, tenant.databaseUsername!!)
                .option(PASSWORD, tenant.databasePassword)
                .option(DATABASE, tenant.databaseName!!)
                .option(Option.valueOf("schema"), r2dbcProperties.properties["schema"]!!)
                .build())
    }

    @EventListener
    fun onTenantAdded(tenant: Tenant): Mono<Void> {
        log.debug("Adding new tenant")
        val connectionFactory = tenantConnectionFactory(tenant)
        TenantWebFilter.lookup.addConnectionFactory("resk", connectionFactory)
        val routingConnectionFactory =
                applicationContext.getBean(TenantAwareRoutingConnectionFactory::class.java)
        routingConnectionFactory.afterPropertiesSet()
        log.debug("Done adding tenant")
        return Mono.empty()
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}