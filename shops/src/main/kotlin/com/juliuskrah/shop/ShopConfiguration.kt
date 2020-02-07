package com.juliuskrah.shop

import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import io.r2dbc.spi.ConnectionFactoryOptions.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository

@Configuration
class ShopConfiguration {
    @Autowired
    private lateinit var clients: ReactiveClientRegistrationRepository

    @Bean
    fun connectionFactory(): ConnectionFactory {
        val connectionFactory = TenantAwareRoutingConnectionFactory()
        val defaultFactory = tenantConnectionFactory("master", "master", "master")
        val factories = mutableMapOf<String, ConnectionFactory>()
        factories["aparel"] = tenantConnectionFactory("aparel", "aparel", "aparel")
        factories["dulcet"] = tenantConnectionFactory("dulcet", "dulcet", "dulcet")
        connectionFactory.setTargetConnectionFactories(factories)
        connectionFactory.setDefaultTargetConnectionFactory(defaultFactory)
        connectionFactory.afterPropertiesSet()
        return connectionFactory;
    }

    fun tenantConnectionFactory(database: String, user: String, password: String): ConnectionFactory {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, user)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .build());
    }
//    @Bean
//    fun securityFilter(http: ServerHttpSecurity): SecurityWebFilterChain {
//        val logoutHandler = OidcClientInitiatedServerLogoutSuccessHandler(clients)
//        logoutHandler.setPostLogoutRedirectUri(URI.create("http://localhost:8082"))
//        val authenticationEntryPoint = RedirectServerAuthenticationEntryPoint("$DEFAULT_AUTHORIZATION_REQUEST_BASE_URI/shop")
//        http.authorizeExchange {
//            it.anyExchange().authenticated()
//        }.logout {
//            it.logoutSuccessHandler(logoutHandler)
//        }.oauth2Login(withDefaults())
//                .exceptionHandling {
//                    it.authenticationEntryPoint(authenticationEntryPoint)
//                }
//        return http.build()
//    }
}