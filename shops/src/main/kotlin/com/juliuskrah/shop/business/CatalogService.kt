package com.juliuskrah.shop.business

import com.juliuskrah.shop.domain.Catalog
import com.juliuskrah.shop.domain.Tenant
import com.juliuskrah.shop.repository.CatalogRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
@Transactional
class CatalogService(private val catalogRepository: CatalogRepository) {
    fun find(): Flux<Catalog> =
            catalogRepository.findAll()

    fun find(id: UUID): Mono<Catalog> =
            catalogRepository.findById(id)

    fun delete(id: UUID): Mono<Void> =
            catalogRepository.deleteById(id)

    fun delete(): Mono<Void> =
            catalogRepository.deleteAll()
}