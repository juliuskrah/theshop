package com.juliuskrah.shop.tenancy

import com.juliuskrah.shop.ApplicationProperties
import org.springframework.core.Ordered
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class HttpHeaderTenantResolverStrategy(val properties: ApplicationProperties) : AbstractTenantResolverStrategy(), Ordered {
    override fun apply(exchange: ServerWebExchange): Pair<CharSequence?, ServerWebExchange> {
        val tenant = toTenant(exchange)
        if (!tenant.isNullOrBlank())
            exchange.response.beforeCommit {
                addTenantHeader(exchange.response, tenant)
            }
        return Pair(tenant, exchange)
    }

    override fun getOrder(): Int {
        return 202
    }

    fun toTenant(exchange: ServerWebExchange): String? {
        val headers = exchange.request.headers
        val headerValue = headers[properties.tenantHeaderKey]
        if (!headerValue.isNullOrEmpty()) return headerValue[0]
        return null
    }

    // todo is this necessary?
    private fun addTenantHeader(response: ServerHttpResponse, tenant: CharSequence?): Mono<Void> {
        return Mono.fromRunnable<Void> {
            response.headers.add(properties.tenantHeaderKey, tenant as String?)
        }
    }
}