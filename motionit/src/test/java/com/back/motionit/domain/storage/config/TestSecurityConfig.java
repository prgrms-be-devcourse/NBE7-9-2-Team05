package com.back.motionit.domain.storage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class TestSecurityConfig {
	@Bean
	SecurityFilterChain testChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.ignoringRequestMatchers("/api/v1/storage/upload-url"))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/api/v1/storage/**").permitAll()
				.anyRequest().authenticated()
			);
		return http.build();
	}
}
