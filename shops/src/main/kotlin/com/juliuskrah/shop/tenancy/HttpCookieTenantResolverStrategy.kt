package com.juliuskrah.shop.tenancy

import com.juliuskrah.shop.ApplicationProperties
import org.springframework.core.Ordered
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class HttpCookieTenantResolverStrategy(val properties: ApplicationProperties) : AbstractTenantResolverStrategy(), Ordered {


    override fun apply(exchange: ServerWebExchange): Pair<CharSequence?, ServerWebExchange> {
        val tenant = toTenant(exchange)
        if (!tenant.isNullOrBlank())
            exchange.response.beforeCommit {
                addTenantCookie(exchange.response, tenant)
            }
        return Pair(tenant, exchange)
    }

    override fun getOrder(): Int {
        return 203
    }

    private fun toTenant(exchange: ServerWebExchange): String? {
        val cookies = exchange.request.cookies
        val cookieValue = cookies[properties.tenantCookieKey]
        if (!cookieValue.isNullOrEmpty()) return cookieValue[0]?.value
        return null
    }

    // todo is this necessary?
    private fun addTenantCookie(response: ServerHttpResponse, tenant: CharSequence?): Mono<Void> {
        return Mono.fromRunnable<Void> {
            val cookie: ResponseCookie = ResponseCookie.from(properties.tenantCookieKey, tenant as String).build()
            response.cookies.add(properties.tenantCookieKey, cookie)
        }
    }
}