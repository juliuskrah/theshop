package com.juliuskrah.shop.web

import com.juliuskrah.shop.business.TenantService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.Rendering

@Controller
class HomeController(private val tenantService: TenantService) {

    @GetMapping(path = ["/"])
    fun welcome(): Rendering = Rendering.view("index").build()

    @GetMapping(path = ["/user"])
    fun tenants(): Rendering = Rendering.view("user")
            .modelAttribute("tenants", tenantService.find())
            .build()
}