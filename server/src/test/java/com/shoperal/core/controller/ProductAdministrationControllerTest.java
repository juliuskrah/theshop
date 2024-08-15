package com.shoperal.core.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Map;

import com.shoperal.core.ApplicationProperties;
import com.shoperal.core.business.PaginationWrapper;
import com.shoperal.core.business.ProductService;
import com.shoperal.core.dto.ProductDTO;

import org.assertj.core.data.Index;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WithMockUser(roles = "ADMIN")
@WebMvcTest(controllers = ProductAdministrationController.class)
public class ProductAdministrationControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ProductService productService;
    @MockBean
    private ApplicationProperties properties;

    @Test
    void testDefaultSort() throws Exception {
        var pagination = new PaginationWrapper<ProductDTO>();
        var products = spy(new ArrayList<ProductDTO>());
        doReturn(5).when(products).size();
        pagination.setContent(products);
        pagination.setAfter("Zm9vYmFy");
        pagination.setHasNext(true);

        when(productService.findProducts(anyMap(), anyInt(), any(Sort.class))) //
                .thenReturn(pagination);

        this.mvc.perform(get("/admin/product") //
                .header("X-Requested-With", "XMLHttpRequest") //
                .param("role", "list")) //
                .andExpect(status().isOk()) //
                .andExpect(model().attribute("products", products)) //
                .andExpect(model().attribute("after", "Zm9vYmFy")) //
                .andExpect(model().attribute("hasNext", true)) //
                .andExpect(model().attributeDoesNotExist("before")) //
                .andExpect(model().attribute("hasPrevious", false));
        var sortCapture = ArgumentCaptor.forClass(Sort.class);
        verify(productService, times(1)).findProducts(anyMap(), anyInt(), sortCapture.capture());
        var sort = sortCapture.getValue();
        assertThat(sort).isNotNull();
        assertThat(sort.isSorted()).isTrue();
        assertThat(sort.stream()).size().isEqualTo(2);
        assertThat(sort.stream()).contains(Order.asc("id"), Index.atIndex(1));
        assertThat(sort.stream()).first().isEqualTo(Order.asc("name"));
    }

    @Test
    void testFirstPageSortNameDescending() throws Exception {
        var pagination = new PaginationWrapper<ProductDTO>();
        var products = spy(new ArrayList<ProductDTO>());
        pagination.setContent(products);
        pagination.setAfter("Zm9vYmFy");
        pagination.setHasNext(true);

        when(productService.findProducts(anyMap(), anyInt(), any(Sort.class))) //
                .thenReturn(pagination);

        this.mvc.perform(get("/admin/product") //
                .header("X-Requested-With", "XMLHttpRequest") //
                .param("role", "list") //
                .param("sort", "name,desc", "id,desc")) //
                .andExpect(status().isOk()) //
                .andExpect(model().attribute("products", products)) //
                .andExpect(model().attribute("after", "Zm9vYmFy")) //
                .andExpect(model().attribute("hasNext", true)) //
                .andExpect(model().attributeDoesNotExist("before")) //
                .andExpect(model().attribute("hasPrevious", false));
        var sortCapture = ArgumentCaptor.forClass(Sort.class);
        verify(productService, times(1)).findProducts(anyMap(), anyInt(), sortCapture.capture());
        var sort = sortCapture.getValue();
        assertThat(sort).isNotNull();
        assertThat(sort.isSorted()).isTrue();
        assertThat(sort.stream()).size().isEqualTo(2);
        assertThat(sort.stream()).last().isEqualTo(Order.desc("id"));
        assertThat(sort.stream()).contains(Order.desc("name"), Index.atIndex(0));
    }

    @Test
    void testNextPageSortNameDescending() throws Exception {
        var pagination = new PaginationWrapper<ProductDTO>();
        var products = spy(new ArrayList<ProductDTO>());
        pagination.setContent(products);
        pagination.setBefore("Zm9v");
        pagination.setHasPrevious(true);

        when(productService.findProducts(anyMap(), anyInt(), any(Sort.class))) //
                .thenReturn(pagination);

        this.mvc.perform(get("/admin/product") //
                .header("X-Requested-With", "XMLHttpRequest") //
                .param("role", "list") //
                .param("after", "Zm9vYmFy") //
                .param("sort", "name,desc", "id,desc")) //
                .andExpect(status().isOk()) //
                .andExpect(model().attribute("products", products)) //
                .andExpect(model().attribute("before", "Zm9v")) //
                .andExpect(model().attribute("hasNext", false)) //
                .andExpect(model().attributeDoesNotExist("after")) //
                .andExpect(model().attribute("hasPrevious", true));
        var sortCapture = ArgumentCaptor.forClass(Sort.class);
        @SuppressWarnings({"unchecked"})
        ArgumentCaptor<Map<String, String>> paramsCaptor = ArgumentCaptor.forClass(Map.class);
        verify(productService, times(1)).findProducts(paramsCaptor.capture(), anyInt(), sortCapture.capture());
        var sort = sortCapture.getValue();
        assertThat(sort).isNotNull();
        assertThat(sort.isSorted()).isTrue();
        assertThat(sort.stream()).size().isEqualTo(2);
        assertThat(sort.stream()).last().isEqualTo(Order.desc("id"));
        assertThat(sort.stream()).contains(Order.desc("name"), Index.atIndex(0));
        var params = paramsCaptor.getValue();
        assertThat(params).isNotEmpty();
        assertThat(params).anySatisfy((key, value) -> {
            assertThat(key).isIn("after", "sort", "role");
            assertThat(value).isIn("Zm9vYmFy");
        });
    }

}
