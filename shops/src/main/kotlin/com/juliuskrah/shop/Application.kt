package com.juliuskrah.shop

import org.springframework.boot.Banner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity

@EnableReactiveMethodSecurity
@SpringBootApplication
class Application

	fun main(args: Array<String>) {
		runApplication<Application>(*args) {
			setBannerMode(Banner.Mode.OFF)
		}
	}
