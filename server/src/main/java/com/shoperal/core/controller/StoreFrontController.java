package com.shoperal.core.controller;

import java.util.List;
import java.util.UUID;

import com.shoperal.core.business.StoreService;
import com.shoperal.core.dto.CategoryDTO;
import com.shoperal.core.dto.ProductDTO;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StoreFrontController {
	static final UUID STORE_ID = UUID.fromString("16e98b25-d8cc-4590-90c4-d0f5dcf78548");
	private final StoreService storeService;
	private static final String INDEX_TEMPLATE = "index";
	private static final String CATEGORY_TEMPLATE = "category";
	private static final String CART_TEMPLATE = "shoping-cart";
	private static final String CATEGORY_DETAILS_TEMPLATE = "shop-details";
	private static final String PRODUCT_TEMPLATE = "product";
	private static final String SEARCH_TEMPLATE = "search";
	private static final String CHECKOUT_TEMPLATE = "checkout";

	@ModelAttribute
	void store(Model model) {
		model.addAttribute("store", storeService.findStoreInfo(STORE_ID));
		model.addAttribute("menus", storeService.findMenusItems("main-menu"));
	}

	/**
	 * Concatenates and returns template
	 * 
	 * @param template
	 * @param fragment
	 * @exception IllegalArgumentException
	 * @return template
	 */
	private String ajax(String template, String fragment) {
		Assert.hasText(template, "'template'must' not be null");
		Assert.hasText(fragment, "'fragment'must' not be null");
		return template.concat(" :: ").concat(fragment);
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("pageTitle", this.storeService.findHomePageTitle(STORE_ID));
		return INDEX_TEMPLATE;
	}

	@GetMapping(path = { "/" }, headers = { "X-Requested-With=XMLHttpRequest" })
	public String indexAjax() {
		return ajax(INDEX_TEMPLATE, "fragment");
	}

	@GetMapping(path = "/signin")
	String signin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (!(authentication instanceof AnonymousAuthenticationToken))
			return "redirect:/";
		return "login";
	}

	@GetMapping("/categories")
	public String category(Model model) {
		var category = new CategoryDTO();
		category.setName("Men's clothing");
		var dummyList = List.of(category);
		model.addAttribute("categories", dummyList);
		// model.addAttribute("categories", categoryService.findCategories());
		return CATEGORY_TEMPLATE;
	}

	@GetMapping(path = { "/categories" }, headers = { "X-Requested-With=XMLHttpRequest" })
	public String categoryAjax(Model model) {
		category(model);
		return ajax(CATEGORY_TEMPLATE, "fragment");
	}

	@GetMapping("/categories/{friendly}")
	public String categoryDetails(@PathVariable String friendly) {
		return CATEGORY_DETAILS_TEMPLATE;
	}

	@GetMapping("/pages/{slug}")
	public String pages(@PathVariable String slug) {
		return "contact";
	}

	@GetMapping("/products")
	public String product(Model model) {
		var product = new ProductDTO();
		product.setId(UUID.randomUUID());
		product.setName("Jordan");
		product.setDescription("Jordan's are cool");
		product.setPrice(35.99F);
		product.setMediaSrc("https://images.unsplash.com/photo-1542291026-7eec264c27ff?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=750&q=80");
		model.addAttribute("product", product);
		return PRODUCT_TEMPLATE;
	}

	@GetMapping(path = { "/products" }, headers = { "X-Requested-With=XMLHttpRequest" })
	public String productAjax(Model model) {
		product(model);
		return ajax(PRODUCT_TEMPLATE, "fragment");
	}

	@GetMapping("/cart")
	public String cart() {
		return CART_TEMPLATE;
	}

	@GetMapping("/checkout")
	public String checkout() {
		return CHECKOUT_TEMPLATE;
	}

	@GetMapping("/search")
	public String search() {
		return SEARCH_TEMPLATE;
	}
}
