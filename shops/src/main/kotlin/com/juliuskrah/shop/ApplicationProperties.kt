package com.juliuskrah.shop

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@ConfigurationProperties("shop")
class ApplicationProperties {
    @NotBlank
    lateinit var domainPattern: String

    @NotBlank
    lateinit var baseDomain: String

    @NotBlank
    lateinit var domainTemplate: String

    @NotBlank
    var databaseHost: String = "postgres"
    var tenantHeaderKey: String = "X-TENANT-ID"
    var tenantCookieKey: String = "TENANT-ID"
}