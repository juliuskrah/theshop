package com.juliuskrah.shop.tenancy

import org.springframework.web.server.ServerWebExchange
import java.util.function.Function

abstract class AbstractTenantResolverStrategy : TenantResolverStrategy, Function<ServerWebExchange, Pair<CharSequence?, ServerWebExchange>> {
    override fun resolveTenant(exchange: ServerWebExchange): Pair<CharSequence?, ServerWebExchange> {
        return apply(exchange)
    }
}