package com.shoperal.core.controller;

import java.net.URI;

import com.shoperal.core.business.StorageService;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@PreAuthorize("isAuthenticated()")
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdministrationController implements ApplicationContextAware {
	private ApplicationContext applicationContext;
	private final Environment env;

	@GetMapping({ "", "/dashboard" })
	String dashboard() {
		return "admin/dashboard";
	}

	@GetMapping("/sale")
	String sale() {
		return "admin/sale";
	}

	@GetMapping("/report")
	String report() {
		return "admin/report";
	}

	@GetMapping("/setting")
	String setting() {
		return "admin/setting";
	}

	@GetMapping("/customer")
	String customer() {
		return "admin/customer";
	}

	@GetMapping("/product")
	String product(Model model) {
		return "admin/product";
	}

	@GetMapping("/extension")
	String extension() {
		return "admin/extension";
	}

	@GetMapping("/marketing")
	String marketing() {
		return "admin/marketing";
	}

	@GetMapping("/presign-image-uri")
	@ResponseBody
	URI presignImageUri(@RequestParam String filename, @RequestParam String contentType) {
		StorageService storageService;
		if (env.acceptsProfiles(Profiles.of(com.shoperal.core.utility.Profiles.LOCAL))) {
			storageService = applicationContext.getBean("fileSystemStorageService", StorageService.class);
			log.debug("loading uri from fileSystemStorageService");
		} else {
			storageService = applicationContext.getBean(StorageService.class);
			log.debug("loading uri from aWSS3StorageService {}", contentType);
		}
		return storageService.getPreSignedURI(filename, contentType);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
