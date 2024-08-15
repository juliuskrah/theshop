package com.shoperal.core.controller;

import java.util.Map;

import com.shoperal.core.ApplicationProperties;
import com.shoperal.core.business.ProductService;
import com.shoperal.core.dto.ProductDTO;

import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.data.web.SortDefault.SortDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Julius Krah
 */
@Slf4j
@Controller
@PreAuthorize("hasAnyRole('ADMIN')")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class ProductAdministrationController {
	private final ProductService productService;
	private final ApplicationProperties properties;

	@PostMapping("/product")
	String addProduct(@ModelAttribute ProductDTO product) {
		log.debug("Saving product {}", product);
		productService.createProduct(product);
		return "redirect:/admin/product";
	}

	@GetMapping("/product/add")
	String product(Model model) {
		return "admin/add_product";
	}

	@GetMapping(path = "/product/add", headers = { "X-Requested-With=XMLHttpRequest" }, params = { "role=form" })
	String productFormAjax(Model model /* RequestParam(CREATE or UPDATE enum) */) {
		log.debug("Loading product form via AJAX");
		model.addAttribute("product", new ProductDTO());
		log.trace("Setting product media upload limits");
		model.addAttribute("productMediaMaxFileSize", properties.getTenant() //
				.getLimits().getProductMediaMaxFileSize().toMegabytes());
		return "admin/add_product :: form";
	}

	@GetMapping(path = "/product", headers = { "X-Requested-With=XMLHttpRequest" }, params = { "role=list" })
	String productListAjax(Model model, @RequestParam Map<String, String> options, //
			@RequestParam(defaultValue = "20") int size, //
			@SortDefaults( //
			{ @SortDefault("name"), @SortDefault("id") } //
			) Sort sort) {
		log.debug("Loading products via AJAX");
		var pagination = productService.findProducts(options, size, sort);
		model.addAttribute("products", pagination.getContent());
		model.addAttribute("after", pagination.getAfter());
		model.addAttribute("before", pagination.getBefore());
		model.addAttribute("hasNext", pagination.isHasNext());
		model.addAttribute("hasPrevious", pagination.isHasPrevious());
		return "admin/product :: list";
	}
}
