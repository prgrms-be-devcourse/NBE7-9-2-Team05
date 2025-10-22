package com.back.motionit.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.back.motionit.domain.auth.social.service.SocialAuthService;
import com.back.motionit.global.error.code.AuthErrorCode;
import com.back.motionit.global.error.exception.BusinessException;
import com.back.motionit.global.request.RequestContext;
import com.back.motionit.global.respoonsedata.ResponseData;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

	private static final String AUTH_PATH_PREFIX = "/api/v1/auth/";
	private static final String BEARER_PREFIX = "Bearer ";

	private final SocialAuthService socialAuthService;
	private final RequestContext requestContext;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		logger.debug("CustomAuthenticationFilter called");

		try {
			authenticate(request, response, filterChain);
		} catch (BusinessException e) {
			writeErrorResponse(response, e);
		}
	}

	private void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String requestURI = request.getRequestURI();

		if (!requestURI.startsWith("/api/")) {
			filterChain.doFilter(request, response);
			return;
		}

		if (requestURI.startsWith(AUTH_PATH_PREFIX)) {
			filterChain.doFilter(request, response);
			return;
		}

		String headerAuthorization = requestContext.getHeader("Authorization", "");

		if (headerAuthorization.isBlank()) {
			throw new BusinessException(AuthErrorCode.AUTH_HEADER_REQUIRED);
		}

		if (!headerAuthorization.startsWith(BEARER_PREFIX)) {
			throw new BusinessException(AuthErrorCode.AUTH_HEADER_INVALID_SCHEME);
		}

		String accessToken = headerAuthorization.substring(BEARER_PREFIX.length());

		Map<String, Object> payload = socialAuthService.payloadOrNull(accessToken);

		if (payload == null) {
			throw new BusinessException(AuthErrorCode.TOKEN_INVALID);
		}

		long userId = ((Number)payload.get("id")).longValue();

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userId,
			null,
			List.of(new SimpleGrantedAuthority("ROLE_USER"))
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}

	private void writeErrorResponse(HttpServletResponse response, BusinessException e) throws IOException {
		ResponseData<Void> body = ResponseData.error(e.getErrorCode());
		response.setStatus(e.getErrorCode().getStatus().value());
		response.setContentType("application/json;charset=UTF-8");
		response.getWriter().write(
			String.format("{\"resultCode\":\"%s\",\"msg\":\"%s\",\"data\":null}",
				body.getResultCode(), body.getMsg())
		);
	}
}

