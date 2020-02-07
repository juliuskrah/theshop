package com.juliuskrah.shop

import com.juliuskrah.shop.security.TenantWebFilter.Companion.routingKey
import org.springframework.data.r2dbc.connectionfactory.lookup.AbstractRoutingConnectionFactory
import reactor.core.publisher.Mono


class TenantAwareRoutingConnectionFactory : AbstractRoutingConnectionFactory() {

    override fun determineCurrentLookupKey(): Mono<Any> {
        return Mono.subscriberContext().filter{
            it.hasKey(routingKey)
        }.map {
            it.get<String>(routingKey)
        };
    }
}