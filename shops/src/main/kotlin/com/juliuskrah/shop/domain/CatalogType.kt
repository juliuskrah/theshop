package com.juliuskrah.shop.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table
data class CatalogType(
        @Id
        val id: UUID,
        val type: String
)
