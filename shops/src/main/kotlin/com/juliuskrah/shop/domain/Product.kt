package com.juliuskrah.shop.domain

import java.util.UUID
import java.net.URI

data class Product(
    val id: UUID,
    val name: String,
    val description: String?,
    val price: Float,
    val imageSrc: URI
)