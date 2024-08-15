package com.shoperal.core.config;

import com.shoperal.core.ApplicationProperties;
import com.shoperal.core.tenancy.TenantFilter;

import nz.net.ultraq.thymeleaf.layoutdialect.LayoutDialect;
import nz.net.ultraq.thymeleaf.layoutdialect.decorators.strategies.GroupingStrategy;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * Web related configuration
 * 
 * @author Julius Krah
 */
@Configuration(proxyBeanMethods = false)
public class WebMvcConfiguration {
    
    @Bean
    LayoutDialect layoutDialect() {
        return new LayoutDialect(new GroupingStrategy());
    }
    
    @Bean
    FilterRegistrationBean<TenantFilter> tenantFilter() {
        return new FilterRegistrationBean<TenantFilter>(new TenantFilter());
    }
    
    @Bean
    FilterRegistrationBean<CorsFilter> corsFilter(ApplicationProperties properties) {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", properties.getCors());
        var bean = new FilterRegistrationBean<CorsFilter>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}
