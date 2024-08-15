package com.shoperal.core.business;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;

import com.shoperal.core.config.WebSecurityConfiguration;
import com.shoperal.core.dto.CategoryDTO;
import com.shoperal.core.repository.CategoryRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest(classes = {CategoryServiceImpl.class, WebSecurityConfiguration.class})
public class CategoryServiceSecurityTest {
	@MockBean
	private CategoryRepository repository;
	@Autowired
	private CategoryService service;

	@Test
	@WithMockUser(username = "juliuskrah")
	void testPreauthorize() {
		var category = new CategoryDTO();
		category.setName("test category");
		category.setFriendlyName("/categories/test-category");
		var throwable = catchThrowableOfType(() -> this.service.createCategory(category), AccessDeniedException.class);
		assertThat(throwable).hasMessageContaining("Access Denied");
	}
}
