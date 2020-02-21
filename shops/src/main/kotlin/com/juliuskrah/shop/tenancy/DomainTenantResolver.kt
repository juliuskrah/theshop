package com.juliuskrah.shop.tenancy

import com.juliuskrah.shop.ApplicationProperties
import org.springframework.core.Ordered
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import java.util.regex.Pattern

@Component
class DomainTenantResolver(val properties: ApplicationProperties) : TenantResolver, Ordered {

    override fun resolveTenant(request: ServerHttpRequest): CharSequence {
        return toTenant(request.uri.host)
    }

    override fun getOrder(): Int {
        return 201
    }

    /**
     * Extracts tenant identifier from host. If the pattern cannot match, then it is the default tenant.
     * Default tenant will return ''
     */
    private fun toTenant(host: String): String {
        val pattern = Pattern.compile(properties.domainPattern)
        val matcher = pattern.matcher(host)
        var tenant = ""
        while (matcher.find()) {
            tenant = matcher.group(1)
        }
        return tenant
    }
}