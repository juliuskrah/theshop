package com.shoperal.core.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalTenantController {
	@ModelAttribute
	void storeSetting(Model model) {
		model.addAttribute("shopEmail", "info@shoperal.com");
		model.addAttribute("shopTelephone", "+233 26.501.8733");
		model.addAttribute("shopAddress", new Address());
	}
	
	static class Address {
		public String line1 = "5th Floor Ghana House, Accra High Street";
	}
}
