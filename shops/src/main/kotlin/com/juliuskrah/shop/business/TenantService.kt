package com.juliuskrah.shop.business

import com.juliuskrah.shop.domain.Tenant
import com.juliuskrah.shop.repository.TenantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Service
@Transactional
class TenantService(private val tenantRepository: TenantRepository) {
    fun find(): Flux<Tenant> =
            tenantRepository.findAll()

    fun find(id: UUID): Mono<Tenant> =
            tenantRepository.findById(id)

    fun delete(id: UUID): Mono<Void> =
            tenantRepository.deleteById(id)

    fun delete(): Mono<Void> =
            tenantRepository.deleteAll()
}