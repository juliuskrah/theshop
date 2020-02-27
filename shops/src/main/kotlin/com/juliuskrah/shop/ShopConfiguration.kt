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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.net.URI
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

@Configuration
@EnableScheduling
@EnableConfigurationProperties(ApplicationProperties::class)
class ShopConfiguration : ApplicationContextAware {
    @Autowired
    private lateinit var clients: ReactiveClientRegistrationRepository
    private lateinit var applicationContext: ApplicationContext
    private val log = LoggerFactory.getLogger(ShopConfiguration::class.java)

    @Bean
    fun connectionFactory(): ConnectionFactory {
        val connectionFactory = TenantAwareRoutingConnectionFactory()
        val defaultFactory = tenantConnectionFactory("master", "master", "master")
        val tenants = tenants(Mono.from(defaultFactory.create())).collectList().block()
        log.debug("discovered tenants {}", tenants)
        val factories = mutableMapOf<String, ConnectionFactory>()
        for (tenant: Tenant in tenants!!) {
            val name = tenant.name
            if (name != null)
                factories[name] = tenantConnectionFactory(name, name, name)
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
            connection.createStatement("SELECT id, name FROM tenant")
                    .execute()
        }.flatMap { result ->
            result.map { row, _ ->
                val id = row.get("id", UUID::class.java)
                val name = row.get("name", String::class.java)
                Tenant(id, name)
            }

        }
    }

    fun tenantConnectionFactory(database: String, user: String, password: String): ConnectionFactory {
        return ConnectionFactories.get(builder()
                .option(DRIVER, "postgresql")
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, user)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .option(Option.valueOf<String>("schema"), "catalogs")
                .build())
    }

    fun replace(variable: String): String {
        val namesPattern: Pattern = Pattern.compile("\\{([^/]+?)\\}")
        val matcher: Matcher = namesPattern.matcher("{name}.theshop.com")
        val sb = StringBuffer()

        if (matcher.find()) {
            matcher.appendReplacement(sb, variable)
        }
        matcher.appendTail(sb)
        println(sb.toString())
        return sb.toString()
    }

    @Bean
    fun securityFilter(http: ServerHttpSecurity): SecurityWebFilterChain {
        val logoutHandler = OidcClientInitiatedServerLogoutSuccessHandler(clients)
        // todo logout uri per tenant
        logoutHandler.setPostLogoutRedirectUri(URI.create("http://localhost:8082"))
        // todo auth entryPoint per tenant
        val authenticationEntryPoint = RedirectServerAuthenticationEntryPoint("/oauth2/authorization/shop")
        http.authorizeExchange {
            it.anyExchange().authenticated()
        }.logout {
            it.logoutSuccessHandler(logoutHandler)
        }.oauth2Login(withDefaults())
                .exceptionHandling {
                    it.authenticationEntryPoint(authenticationEntryPoint)
                }
        return http.build()
    }

    @Scheduled(initialDelay = 60000, fixedRate = 600000)
    fun dynamicRegistrationOfDataSource() {
        log.debug("Adding resk")
        val connectionFactory = tenantConnectionFactory("resk", "resk", "resk")
        TenantWebFilter.lookup.addConnectionFactory("resk", connectionFactory)
        val routingConnectionFactory = applicationContext.getBean(TenantAwareRoutingConnectionFactory::class.java)
        routingConnectionFactory.afterPropertiesSet()
        log.debug("Done adding resk")
    }

    override fun setApplicationContext(applicationContext: ApplicationContext) {
        this.applicationContext = applicationContext
    }
}