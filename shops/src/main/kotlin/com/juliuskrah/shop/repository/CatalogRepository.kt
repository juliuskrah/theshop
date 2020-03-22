package com.juliuskrah.shop.repository

import com.juliuskrah.shop.domain.Catalog
import com.juliuskrah.shop.domain.Tenant
import org.springframework.data.r2dbc.repository.R2dbcRepository
import java.util.*

interface CatalogRepository : R2dbcRepository<Catalog, UUID>