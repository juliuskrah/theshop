package com.juliuskrah.shop.repository

import com.juliuskrah.shop.security.TenantWebFilter
import org.springframework.data.r2dbc.connectionfactory.lookup.AbstractRoutingConnectionFactory
import reactor.core.publisher.Mono


class TenantAwareRoutingConnectionFactory : AbstractRoutingConnectionFactory() {

    override fun determineCurrentLookupKey(): Mono<Any> {
        return Mono.subscriberContext().filter {
            it.hasKey(TenantWebFilter.routingKey)
        }.map {
            it.get<String>(TenantWebFilter.routingKey)
        }
    }
}