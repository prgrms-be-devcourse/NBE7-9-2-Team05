package com.back.motionit.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.back.motionit.security.CustomAuthenticationFilter;
import com.back.motionit.security.handler.CustomOAuth2LoginSuccessHandler;
import com.back.motionit.security.oauth.CustomOAuth2AuthorizationRequestResolver;
import com.back.motionit.security.oauth.CustomOAuth2UserService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final CustomOAuth2LoginSuccessHandler customOAuth2LoginSuccessHandler;
	private final CustomAuthenticationFilter customAuthenticationFilter;
	private final CustomOAuth2AuthorizationRequestResolver customOAuth2AuthorizationRequestResolver;
	private final CustomOAuth2UserService customOAuth2UserService;

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.cors(Customizer.withDefaults())
			.authorizeHttpRequests((authorizeHttpRequests) -> authorizeHttpRequests
				.requestMatchers("/favicon.ico").permitAll()
				.requestMatchers("/h2-console/**").permitAll()
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
				.requestMatchers("/.well-known/**").permitAll()
				.requestMatchers("/api/v1/storage/**").permitAll()
				.requestMatchers("/api/v1/auth/**").permitAll()
				.anyRequest().authenticated())
			.csrf((csrf) -> csrf.disable())
			.headers((headers) -> headers
				.addHeaderWriter(new XFrameOptionsHeaderWriter(
					XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN)))
			.addFilterBefore(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.sessionManagement(sessionManagement ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.oauth2Login(oauth2 -> {
				oauth2
					.successHandler(customOAuth2LoginSuccessHandler)
					.authorizationEndpoint(authorizationEndPoint ->
						authorizationEndPoint.authorizationRequestResolver(customOAuth2AuthorizationRequestResolver)
					)
					.userInfoEndpoint(userInfoEndpoint ->
						userInfoEndpoint.userService(customOAuth2UserService)
					);
			});
		return http.build();
	}

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOriginPatterns(List.of("http://localhost:3000"));
		configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(List.of("*"));
		configuration.setAllowCredentials(true);
		configuration.setExposedHeaders(List.of("*"));

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public AuthorizationManager<Message<?>> messageAuthorizationManager(
	) {
		MessageMatcherDelegatingAuthorizationManager.Builder messages =
			MessageMatcherDelegatingAuthorizationManager.builder();

		messages
			// 구독(/topic/**) 경로별 권한
			.simpSubscribeDestMatchers("/topic/challenge/rooms").permitAll() // 전체 방 목록: 게스트 허용
			.simpSubscribeDestMatchers("/topic/challenge/rooms/*").authenticated() // 로그인만 허용
			// 전송(/app/**) 경로별 권한
			.simpDestMatchers("/app/**").authenticated()
			// 나머지 모두 차단
			.anyMessage().denyAll();

		return messages.build();
	}
}
