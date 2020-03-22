package com.juliuskrah.shop.web

import com.juliuskrah.shop.business.CatalogService
import com.juliuskrah.shop.support.web.ShopUtilities
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.Rendering

@Controller
class HomeController(private val catalogService: CatalogService) {

    @GetMapping(path = ["/"])
    fun welcome(): Rendering = Rendering.view(ShopUtilities.indexViewName).build()

    @GetMapping(path = ["/catalog/items"])
    fun catalogs(): Rendering = getCatalog(ShopUtilities.catalogViewName)

    @GetMapping(path = ["/catalog/items"], headers = ["X-Requested-With=XMLHttpRequest"])
    fun catalogsAjax(): Rendering = getCatalog(ShopUtilities.catalogViewName + " :: frag")

    private fun getCatalog(template: String): Rendering = Rendering.view(template)
            .modelAttribute("catalog_items", catalogService.find())
            .build()
}