package com.shoperal.core.listener;

import java.util.Map;

import com.shoperal.core.event.CategoryEvent;

import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class EventListeners {
	private final DummyKafkaPublisher kafka;

	@EventListener
	public void onSaveCategory(CategoryEvent event) {
		kafka.sendToKafka();
	}

	/**
	 * Get all enpoints. Userful for slash commands
	 * @see https://www.baeldung.com/spring-boot-get-all-endpoints
	 */
	@EventListener
	public void onContextRefreshed(ContextRefreshedEvent event) {
		ApplicationContext applicationContext = event.getApplicationContext();
    	RequestMappingHandlerMapping requestMappingHandlerMapping = applicationContext
        	.getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class);
    	Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping
        	.getHandlerMethods();
    	map.forEach((key, value) -> log.info("{} {}", key, value));
	}
}
