package com.juliuskrah.shop.security

import com.juliuskrah.shop.repository.TenantNotFoundException
import com.juliuskrah.shop.tenancy.TenantResolverStrategy
import org.slf4j.LoggerFactory
import org.springframework.data.r2dbc.connectionfactory.lookup.MapConnectionFactoryLookup
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap

/**
 * Filter to add a lookupKey for the connectionFactory.
 */
@Component
class TenantWebFilter(
        val tenantResolverStrategies: Collection<TenantResolverStrategy>
) : WebFilter {
    /**
     * A cache that stores the host as key and tenant as value
     */
    private val tenantsCache = ConcurrentHashMap<String, String>()

    companion object {
        lateinit var routingKey: String
        val lookup: MapConnectionFactoryLookup = MapConnectionFactoryLookup()
        private val log = LoggerFactory.getLogger(TenantWebFilter::class.java)
    }

    init {
        routingKey = "shopKey"
    }

    /**
     * This filter adds the tenant name in the Context as a lookupKey. When the default tenant is used,
     * the context is not mutated (thus no lookupKey).
     *
     * When the tenant is unknown, the application will throw IllegalStateException because the lookupKey is not
     * found for the connectionFactory.
     *
     * When the default tenant makes a request, because the context remains untouched, we fetch the default
     * connectionFactory registered in the AbstractRoutingConnectionFactory
     */
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val url = exchange.request.uri
        log.debug("request URI : {}", url)
        var transformedExchange = exchange.mutate().build()
        val tenant = tenantsCache.computeIfAbsent(url.host) {
            val pair = toPair(exchange)
            transformedExchange = pair.second
            pair.first as String
        }
        return chain.filter(transformedExchange)
                .subscriberContext { ctx ->
                    if (tenant != "")
                        ctx.put(routingKey, tenant)
                    else ctx
                }
    }

    /**
     * Runs through all registered tenantResolverStrategies until the first strategy that can resolve
     * the tenant. Once resolved, the tenant is cached
     */
    fun toPair(exchange: ServerWebExchange): Pair<CharSequence?, ServerWebExchange> {
        for (tenantResolverStrategy: TenantResolverStrategy in this.tenantResolverStrategies) {
            val pair = tenantResolverStrategy.resolveTenant(exchange)
            if (pair.first != null) return pair
        }
        throw TenantNotFoundException("Tenant cannot be resolved by any of the available configured strategies")
    }
}