package com.shoperal.core.business;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.shoperal.core.event.CategoryEvent;
import com.shoperal.core.listener.DummyKafkaPublisher;
import com.shoperal.core.listener.EventListeners;
import com.shoperal.core.model.Category;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * @author Julius Krah
 */
class CategoryServiceIT {
	private final ApplicationContextRunner runner = new ApplicationContextRunner();
	@Mock
	private DummyKafkaPublisher kafka;
	private AutoCloseable closeable;

	@BeforeEach
	void injectMocks() {
		closeable = MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void releaseMocks() throws Exception {
		closeable.close();
	}

	@Test
	void testCategorySavedEvent() {
		var category = new Category();
		category.setName("Rovers");
		runner
		    .withBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class, () -> new RequestMappingHandlerMapping())
			.withBean(EventListeners.class, kafka) //
				.run(context -> {
					context.publishEvent(new CategoryEvent(this, category));
					verify(kafka, times(1)).sendToKafka();
				});
	}

}
