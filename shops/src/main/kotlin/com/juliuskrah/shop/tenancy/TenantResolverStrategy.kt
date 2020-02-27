package com.juliuskrah.shop.tenancy

import org.springframework.web.server.ServerWebExchange

/**
 * Implementations of this class resolves the current tenant
 */
interface TenantResolverStrategy {
    /**
     * Resolves the tenant from the request. Three implementations are provided out of the box.
     * <ol>
     *     <li>
     *         DomainTenantResolverStrategy: - Resolves the current tenant from the domain. This is usually
     *         resolved from the subdomain. Given the domain <b>example.com</b> and <b>acme.example.com</b>,
     *         <b>acme</b> is the tenant
     *     </li>
     *     <li>
     *         HttpHeaderTenantResolverStrategy: - Resolves the current tenant from the Http Header. The default header
     *         key used is 'X-TENANT-ID'
     *     </li>
     *     <li>
     *         HttpCookieTenantResolverStrategy: - Resolves the current tenant from the Http Cookie. The default cookie
     *         key used is 'TENANT-ID'
     *     </li>
     * </ol>
     * The first successful resolver 'wins'
     */
    fun resolveTenant(exchange: ServerWebExchange): Pair<CharSequence?, ServerWebExchange>
}