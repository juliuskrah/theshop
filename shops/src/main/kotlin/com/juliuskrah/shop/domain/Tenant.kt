package com.juliuskrah.shop.domain

import org.springframework.data.relational.core.mapping.Table
import java.util.*

@Table
data class Tenant(
        val id: UUID,
        val name: String,
        val databaseHostName: String?,
        val databasePort: Int,
        val databaseUsername: String?,
        val databasePassword: String,
        val databaseName: String?)
