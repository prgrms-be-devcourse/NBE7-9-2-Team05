package com.back.motionit.security.oauth;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2AuthorizationRequestResolver implements OAuth2AuthorizationRequestResolver {

	private final ClientRegistrationRepository clientRegistrationRepository;

	@Value("${app.oauth2.redirect-url}")
	private String frontendRedirectUrl;

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request) {
		OAuth2AuthorizationRequest req = new DefaultOAuth2AuthorizationRequestResolver(
			clientRegistrationRepository,
			OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
		).resolve(request);

		return customizeState(req, request);
	}

	@Override
	public OAuth2AuthorizationRequest resolve(HttpServletRequest request, String clientRegistrationId) {
		OAuth2AuthorizationRequest req = new DefaultOAuth2AuthorizationRequestResolver(
			clientRegistrationRepository,
			OAuth2AuthorizationRequestRedirectFilter.DEFAULT_AUTHORIZATION_REQUEST_BASE_URI
		).resolve(request);

		return customizeState(req, request);
	}

	private OAuth2AuthorizationRequest customizeState(OAuth2AuthorizationRequest authorizationRequest,
		HttpServletRequest req) {

		//OAuth 요청이 아닐 때는 넘어감
		if (authorizationRequest == null) {
			return null;
		}

		String redirectUrl = req.getParameter("redirectUrl");

		if (redirectUrl == null) {
			redirectUrl = frontendRedirectUrl;
		}

		String originState = authorizationRequest.getState();
		String newState = originState + "#" + redirectUrl;

		// 특수문자가 포함된 경우 Base64로 인코딩
		String encodedNewState = Base64.getUrlEncoder().encodeToString(newState.getBytes(StandardCharsets.UTF_8));

		return OAuth2AuthorizationRequest.from(authorizationRequest)
			.state(encodedNewState)
			.build();
	}
}
