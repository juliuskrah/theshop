package com.shoperal.core.config;

import com.shoperal.core.ApplicationProperties;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import lombok.RequiredArgsConstructor;

/**
 * This class provides a means for template developers to extend the UI of
 * shoperal.
 * <p>
 * Once a new template is installed by a merchant, we will dynamically load the
 * template location and reload the application context
 * 
 * @author Julius Krah
 */
@RequiredArgsConstructor
@Configuration(proxyBeanMethods = false)
public class ThymeleafConfiguration {
    private final ApplicationContext applicationContext;

    @Bean
    SpringResourceTemplateResolver defaultTemplateResolver(ThymeleafProperties properties) {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setApplicationContext(this.applicationContext);
        resolver.setPrefix(properties.getPrefix());
        resolver.setSuffix(properties.getSuffix());
        resolver.setTemplateMode(properties.getMode());
        if (properties.getEncoding() != null) {
            resolver.setCharacterEncoding(properties.getEncoding().name());
        }
        resolver.setCacheable(properties.isCache());
        Integer order = properties.getTemplateResolverOrder();
        if (order != null) {
            resolver.setOrder(order);
        }
        resolver.setCheckExistence(properties.isCheckTemplate());
        resolver.setUseDecoupledLogic(true);
        return resolver;
    }

    @Configuration(proxyBeanMethods = false)
    @RequiredArgsConstructor
    @ConditionalOnProperty(name = "shoperal.tenant.theme.enable", matchIfMissing = false)
    static class ThymeleafAdditionalLocation {
        private final ApplicationContext applicationContext;
        private final ThymeleafProperties properties;

        @Bean
        SpringResourceTemplateResolver additionalTemplateResolver(ApplicationProperties properties) {
            SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
            resolver.setApplicationContext(this.applicationContext);
            resolver.setPrefix(properties.getTenant().getTheme().getPrefix());
            resolver.setSuffix(properties.getTenant().getTheme().getSuffix());
            resolver.setTemplateMode(TemplateMode.HTML);
            resolver.setCharacterEncoding("UTF-8");
            resolver.setCacheable(this.properties.isCache());
            resolver.setOrder(1);
            resolver.setCheckExistence(true);
            resolver.setUseDecoupledLogic(true);
            return resolver;
        }
    }

}
