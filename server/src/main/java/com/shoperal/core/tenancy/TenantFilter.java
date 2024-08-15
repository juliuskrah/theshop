package com.shoperal.core.tenancy;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

/**
 * Here we determine the current tenant from the request
 * 
 * @author Julius Krah
 */
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        log.trace("Host: {} - Path: {}", request.getServerName(), request.getServletPath());
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        TenantContextHolder.clearContext();
    }

}
