package com.juliuskrah.shop.tenancy

import com.juliuskrah.shop.ApplicationProperties
import org.springframework.core.Ordered
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component

@Component
class HttpHeaderTenantResolverStrategy(val properties: ApplicationProperties) : TenantResolverStrategy, Ordered {

    override fun resolveTenant(request: ServerHttpRequest): CharSequence? {
        val headers = request.headers
        return toTenant(headers)
    }

    override fun getOrder(): Int {
        return 202
    }

    fun toTenant(headers: HttpHeaders): String? {
        val headerValue = headers[properties.tenantHeaderKey]
        if (!headerValue.isNullOrEmpty()) return headerValue[0]
        return null
    }
}