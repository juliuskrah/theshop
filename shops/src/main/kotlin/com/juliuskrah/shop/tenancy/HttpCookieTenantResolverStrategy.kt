package com.juliuskrah.shop.tenancy

import com.juliuskrah.shop.ApplicationProperties
import org.springframework.core.annotation.Order
import org.springframework.http.HttpCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap

@Order(203)
@Component
class HttpCookieTenantResolverStrategy(val properties: ApplicationProperties) : TenantResolverStrategy {

    override fun resolveTenant(request: ServerHttpRequest): CharSequence? {
        val cookies = request.cookies
        return toTenant(cookies)
    }

    private fun toTenant(cookies: MultiValueMap<String, HttpCookie>): String? {
        val cookieValue = cookies[properties.tenantCookieKey]
        if (!cookieValue.isNullOrEmpty()) return cookieValue[0]?.value
        return null
    }
}