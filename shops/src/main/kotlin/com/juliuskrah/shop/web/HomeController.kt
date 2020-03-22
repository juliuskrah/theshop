package com.juliuskrah.shop.web

import com.juliuskrah.shop.business.CatalogService
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.Rendering

@Controller
class HomeController(private val catalogService: CatalogService) {

    @GetMapping(path = ["/"])
    fun welcome(): Rendering = Rendering.view("index").build()

    @GetMapping(path = ["/catalog/items"])
    fun tenants(): Rendering = Rendering.view("catalogs")
            .modelAttribute("catalog_items", catalogService.find())
            .build()
}