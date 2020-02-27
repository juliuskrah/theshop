package com.juliuskrah.shop.tenancy

import com.juliuskrah.shop.ApplicationProperties
import org.springframework.core.Ordered
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.regex.Pattern

@Component
class DomainTenantResolverStrategy(val properties: ApplicationProperties) : AbstractTenantResolverStrategy(), Ordered {

    override fun apply(exchange: ServerWebExchange): Pair<CharSequence?, ServerWebExchange> {
        val tenant = toTenant(exchange.request.uri.host)
        if (!tenant.isNullOrBlank())
            exchange.response.beforeCommit {
                addTenantHeaderAndCookie(exchange.response, tenant)
            }
        return Pair(tenant, exchange)
    }

    override fun getOrder(): Int {
        return 201
    }

    /**
     * Extracts tenant identifier from host. If the pattern cannot match, then it is the default tenant.
     * Default tenant will return ''
     */
    private fun toTenant(host: String): String? {
        if (properties.baseDomain == host) return ""

        val pattern = Pattern.compile(properties.domainPattern)
        val matcher = pattern.matcher(host)
        var tenant: String? = null
        while (matcher.find()) {
            tenant = matcher.group(1)
        }
        return tenant
    }

    private fun addTenantHeaderAndCookie(response: ServerHttpResponse, tenant: CharSequence?): Mono<Void> {
        return Mono.fromRunnable<Void> {
            response.headers.add(properties.tenantHeaderKey, tenant as String?)
            val cookie: ResponseCookie = ResponseCookie.from(properties.tenantCookieKey, tenant as String).build()
            response.cookies.add(properties.tenantCookieKey, cookie)
        }
    }
}