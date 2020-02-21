package com.juliuskrah.shop.security

import com.juliuskrah.shop.ApplicationProperties
import com.juliuskrah.shop.tenancy.TenantResolver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.concurrent.ConcurrentHashMap
import java.util.regex.Pattern

/**
 * Filter to add a lookupKey for the connectionFactory.
 */
@Component
class TenantWebFilter(
        val properties: ApplicationProperties,
        val tenantResolvers: Collection<TenantResolver>
) : WebFilter {
    /**
     * A cache that stores the host as key and tenant as value
     */
    private val tenantsCache = ConcurrentHashMap<String, String> ()
    companion object {
        lateinit var routingKey: String
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
        // log.debug("1 {}", tenantResolvers.toList()[0])
        // log.debug("2 {}", tenantResolvers.toList()[1])
        // log.debug("3 {}", tenantResolvers.toList()[2])
        val tenant: String = toTenant(url.host)
        return chain.filter(exchange)
                .subscriberContext { ctx ->
                    if (tenant != "")
                        ctx.put(routingKey, tenant)
                    else ctx
                }
    }

    /**
     * Extracts tenant identifier from host. If the pattern cannot match, then it is the default tenant.
     * Default tenant will return ''
     */
    fun toTenant(host: String): String {
        // tenantResolvers
        val pattern = Pattern.compile(properties.domainPattern)
        val matcher = pattern.matcher(host)
        var tenant = ""
        while (matcher.find()) {
            tenant = matcher.group(1)
        }
        return tenant
    }
}