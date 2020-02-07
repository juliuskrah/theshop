package com.juliuskrah.shop.security

import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class TenantWebFilter : WebFilter {
    companion object {
        lateinit var routingKey: String
    }

    init {
        routingKey = "shopKey"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        // TODO fetch tenant from host
        val tenants = exchange.request.queryParams["tenant"]
        var tenant: String = "master"
        if (tenants != null) {
            tenant = if(tenants.isEmpty()) {
                "master"
            } else
                tenants[0]
        }
        return chain.filter(exchange)
                .subscriberContext { ctx ->
                    ctx.put(routingKey, tenant)
                }
    }
}