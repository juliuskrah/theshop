package com.juliuskrah.shop

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.support.beans
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint
import java.net.URI

@EnableReactiveMethodSecurity
@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args) {
        setBannerMode(Banner.Mode.OFF)
        addInitializers(
                beans {
                    bean {
                        val http = ref<ServerHttpSecurity>()
                        val logoutHandler = OidcClientInitiatedServerLogoutSuccessHandler(ref())
                        // todo logout uri per tenant
                        logoutHandler.setPostLogoutRedirectUri(URI.create("http://theshop.com"))
                        // todo auth entryPoint per tenant
                        val authenticationEntryPoint = RedirectServerAuthenticationEntryPoint("/oauth2/authorization/shop")
                        http.authorizeExchange {
                            it.anyExchange().authenticated()
                        }.logout {
                            it.logoutSuccessHandler(logoutHandler)
                        }.oauth2Login(Customizer.withDefaults())
                                .exceptionHandling {
                                    it.authenticationEntryPoint(authenticationEntryPoint)
                                }
                        http.build()
                    }
                }
        )
    }
}
