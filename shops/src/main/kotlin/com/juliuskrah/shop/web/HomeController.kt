package com.juliuskrah.shop.web

import com.juliuskrah.shop.business.CatalogService
import com.juliuskrah.shop.domain.Product
import com.juliuskrah.shop.support.web.ShopUtilities
import java.net.URI
import java.util.UUID
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.reactive.result.view.Rendering

@Controller
class HomeController(private val catalogService: CatalogService) {

    @GetMapping(path = ["/"])
    fun welcome(): Rendering = Rendering.view(ShopUtilities.indexViewName).build()

    private fun getCatalog(template: String): Rendering = Rendering.view(template)
            .modelAttribute("catalog_items", catalogService.find())
            .build()

    @GetMapping(path = ["/products"])
    fun products(): Rendering {
        val imageSrc = URI.create("https://images.unsplash.com/photo-1542291026-7eec264c27ff?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80")
        val product = Product(UUID.randomUUID(), "Jordan", "Jordan's are cool", 35.99F, imageSrc)
        return Rendering.view(ShopUtilities.productViewName) //
            .modelAttribute("product", product) //
            .build()
    }

    @GetMapping(path = ["/cart"])
    fun cart(): Rendering = Rendering.view(ShopUtilities.cartViewName).build()

    @GetMapping(path = ["/checkout"])
    fun checkout(): Rendering = Rendering.view(ShopUtilities.checkoutViewName).build()

    @GetMapping(path = ["/catalog/items"])
    fun catalogs(): Rendering = getCatalog(ShopUtilities.catalogViewName)

    @GetMapping(path = ["/catalog/items"], headers = ["X-Requested-With=XMLHttpRequest"])
    fun catalogsAjax(): Rendering = getCatalog(ShopUtilities.catalogViewName + " :: frag")
}
