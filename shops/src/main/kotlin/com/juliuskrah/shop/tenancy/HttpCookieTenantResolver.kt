package com.juliuskrah.shop.tenancy

import com.juliuskrah.shop.ApplicationProperties
import org.springframework.core.Ordered
import org.springframework.http.HttpCookie
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap

@Component
class HttpCookieTenantResolver(val properties: ApplicationProperties) : TenantResolver, Ordered {

    override fun resolveTenant(request: ServerHttpRequest): CharSequence {
        val cookies = request.cookies
        return toTenant(cookies)
    }

    override fun getOrder(): Int {
        return 203
    }

    private fun toTenant(cookies: MultiValueMap<String, HttpCookie>): String {
        val cookieValue = cookies[properties.tenantCookieKey]
        if (cookieValue?.size!! > 0) return cookieValue[0].value
        return ""
    }
}