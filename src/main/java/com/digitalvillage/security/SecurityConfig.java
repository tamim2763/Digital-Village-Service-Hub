package com.digitalvillage.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Public starter security configuration.
 * Authentication and authorization rules will be added later.
 */
@Configuration
public class SecurityConfig {

	@Bean
	UserDetailsService userDetailsService() {
		// Authentication will be wired here once real user management is introduced.
		return new InMemoryUserDetailsManager();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
		return http.build();
	}
}
