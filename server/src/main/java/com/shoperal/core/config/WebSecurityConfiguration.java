package com.shoperal.core.config;


import static org.springframework.security.config.Customizer.withDefaults;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfiguration {

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests((authz) -> authz
				.requestMatchers(EndpointRequest.toAnyEndpoint())
				.hasAnyRole("ENDPOINT_ADMIN")
                .anyRequest().permitAll())
			.formLogin(form -> form.loginPage("/signin").permitAll())
			.csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
			.httpBasic(withDefaults());
        return http.build();
    }

	@SuppressWarnings("deprecation")
	@Bean
	public UserDetailsService users() {
		UserBuilder users = User.withDefaultPasswordEncoder();
		UserDetails admin = users //
				.username("juliuskrah") //
				.password("julius123") //
				.roles("USER", "ADMIN") //
				.build();
		UserDetails user = users //
				.username("michael") //
				.password("password") //
				.roles("USER") //
				.build();
		UserDetails actuator = users //
				.username("actuator") //
				.password("actuator") //
				.roles("ENDPOINT_ADMIN") //
				.build();
		return new InMemoryUserDetailsManager(user, admin, actuator);
	}
}
