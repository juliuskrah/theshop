package com.juliuskrah.shop.repository

import com.juliuskrah.shop.domain.Tenant
import org.springframework.data.r2dbc.repository.R2dbcRepository
import java.util.*

interface TenantRepository : R2dbcRepository<Tenant, UUID> {
}