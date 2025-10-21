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

	private final SocialAuthService socialAuthService;
	private final RequestContext requestContext;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws
		ServletException,
		IOException {
		logger.debug("CustomAuthenticationFilter called");

		try {
			authenticate(request, response, filterChain);
		} catch (BusinessException e) {
			ResponseData<Void> body = ResponseData.error(e.getErrorCode());
			response.setStatus(e.getErrorCode().getStatus().value());
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(("""
				{"resultCode":"%s","msg":"%s","data":null}
				""").formatted(body.getResultCode(), body.getMsg()));
		}
	}

	private void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
		throws ServletException, IOException {

		String requestURI = request.getRequestURI();

		// 공개 엔드포인트
		if (!requestURI.startsWith("/api/")) {
			filterChain.doFilter(request, response);
			return;
		}
		// 인증 불필요 API
		if (requestURI.startsWith("/api/v1/auth/")) {
			filterChain.doFilter(request, response);
			return;
		}

		// 토큰 추출
		String headerAuthorization = requestContext.getHeader("Authorization", "");

		if (headerAuthorization.isBlank()) {
			throw new BusinessException(AuthErrorCode.AUTH_HEADER_REQUIRED);
		}

		if (!headerAuthorization.startsWith("Bearer ")) {
			throw new BusinessException(AuthErrorCode.AUTH_HEADER_INVALID_SCHEME);
		}

		String accessToken = headerAuthorization.substring(7);

		// 토큰 검증
		Map<String, Object> payload = socialAuthService.payloadOrNull(accessToken);

		if (payload == null) {
			throw new BusinessException(AuthErrorCode.TOKEN_INVALID);
		}

		long userId = ((Number)payload.get("id")).longValue();

		// SecurityContext에 인증 정보 설정
		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userId,
			null,
			List.of(new SimpleGrantedAuthority("ROLE_USER"))
		);

		SecurityContextHolder.getContext().setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}
