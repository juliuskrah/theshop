package com.juliuskrah.shop.security

import com.juliuskrah.shop.tenancy.TenantResolverStrategy
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.ObjectProvider
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import java.util.concurrent.ConcurrentHashMap

class TenantWebFilterTest {
    @Mock
    private lateinit var tenantResolverStrategy: ObjectProvider<TenantResolverStrategy>
    @Mock
    private lateinit var tenantsCache: ConcurrentHashMap<String, String>
    @InjectMocks
    private val webFilter: TenantWebFilter = TenantWebFilter(tenantResolverStrategy)

    @BeforeAll
    fun init() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun `given a cache should populate cache`() {
        // Mockito.`when`(tenantResolverStrategy.orderedStream()).thenReturn("")
        val exchange = mock(ServerWebExchange::class.java)
        val chain = mock(WebFilterChain::class.java)
        webFilter.filter(exchange, chain)
    }
}