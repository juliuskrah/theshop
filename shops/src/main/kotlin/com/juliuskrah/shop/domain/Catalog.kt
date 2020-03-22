package com.juliuskrah.shop.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import java.util.*

@Table("catalog_item")
data class Catalog(
        @Id
        val id: UUID,
        val name: String,
        val description: String?,
        val price: BigDecimal,
        val pictureFileName: String?,
        val catalogTypeId: UUID,
        val catalogBrandId: UUID,
        val availableStock: Int,
        val restockThreshold: Int,
        val maxStockThreshold: Int,
        val onReorder: Boolean
)