package com.juliuskrah.shop.security

import org.springframework.context.event.EventListener
import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import reactor.core.publisher.Mono
import kotlin.reflect.jvm.internal.impl.load.kotlin.JvmType

abstract class TenantAwareReactiveClientRegistrationRepository :
        ReactiveClientRegistrationRepository, Iterable<ClientRegistration> {
    private lateinit var clientIdToClientRegistration: Map<String, ClientRegistration>

    override fun findByRegistrationId(registrationId: String?): Mono<ClientRegistration> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun iterator(): Iterator<ClientRegistration> {
        return clientIdToClientRegistration.values.iterator()
    }

    @EventListener
    fun onTenantAdded(args: JvmType.Object): Mono<Void> {
        return Mono.empty()
    }
}