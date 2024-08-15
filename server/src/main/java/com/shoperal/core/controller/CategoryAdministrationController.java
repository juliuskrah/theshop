package com.shoperal.core.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Julius Krah
 */
@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping("/admin")
public class CategoryAdministrationController {

	@GetMapping("/category")
	String listCategory() {
		return "admin/category";
	}

}
