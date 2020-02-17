package com.juliuskrah.shop

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@ConfigurationProperties("shop")
class ApplicationProperties {
    @NotBlank
    lateinit var domainPattern: String
}