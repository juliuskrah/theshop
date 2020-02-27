package com.juliuskrah.shop.security

import org.springframework.security.oauth2.client.registration.ClientRegistration
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import reactor.core.publisher.Mono

abstract class TenantAwareReactiveClientRegistrationRepository :
        ReactiveClientRegistrationRepository, Iterable<ClientRegistration> {
    private lateinit var clientIdToClientRegistration: Map<String, ClientRegistration>

    override fun findByRegistrationId(registrationId: String?): Mono<ClientRegistration> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun iterator(): Iterator<ClientRegistration> {
        return clientIdToClientRegistration.values.iterator()
    }
}