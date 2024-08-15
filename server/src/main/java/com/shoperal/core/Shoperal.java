package com.shoperal.core;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author Julius Krah
 */
@SpringBootApplication(proxyBeanMethods = false)
@EnableConfigurationProperties(ApplicationProperties.class)
public class Shoperal {

	public static void main(String[] args) {
		var application = new SpringApplication(Shoperal.class);
		BufferingApplicationStartup applicationStartup = new BufferingApplicationStartup(2048);
		application.setApplicationStartup(applicationStartup);
		application.run(args);
	}

}