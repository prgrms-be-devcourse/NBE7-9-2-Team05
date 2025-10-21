package com.back.motionit.security;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.back.motionit.domain.auth.social.service.SocialAuthService;
import com.back.motionit.domain.user.entity.User;
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
			response.setStatus(body.getStatusCode());
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().write(("""
				{"resultCode":"%s","msg":"%s","data":null}
				""").formatted(body.getResultCode(), body.getMsg()));
		} catch (Exception e) {
			throw e;
		}

	}

	private void authenticate(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws
		ServletException,
		IOException {

		String requestURI = request.getRequestURI();

		if (!request.getRequestURI().startsWith("/api/")) {
			filterChain.doFilter(request, response);
			return;
		}
		// 회원가입/로그인 같은 공개 엔드포인트도 통과
		if (requestURI.startsWith("/api/v1/")) {
			filterChain.doFilter(request, response);
			return;
		}

		String accessToken;

		// 1. 토큰 꺼내서(헤더 → 쿠키 순) 검증
		String headerAuthorization = requestContext.getHeader("Authorization", "");

		if (!headerAuthorization.isBlank()) {
			if (!headerAuthorization.startsWith("Bearer "))
				throw new BusinessException(AuthErrorCode.AUTH_HEADER_INVALID_SCHEME);

			String[] headerAuthorizationBits = headerAuthorization.split(" ");

			accessToken = headerAuthorizationBits[1];
		} else {
			throw new BusinessException(AuthErrorCode.AUTH_HEADER_REQUIRED);
		}

		// 토큰 존재 여부 판단
		// 없으면 익명으로 통과(보호 API면 뒤에서 401/403).
		boolean isAccessTokenExists = !accessToken.isBlank();
		if (!isAccessTokenExists) {
			filterChain.doFilter(request, response);
			return;
		}

		// 2. 토큰 유효성 검사 & 사용자 로딩
		User user = null;
		if (isAccessTokenExists) {
			Map<String, Object> payload = socialAuthService.payloadOrNull(accessToken);

			if (payload != null) {
				long id = (long)payload.get("id");
				String nickname = (String)payload.get("nickname");

				user = new User(id, nickname);
			} else {
				throw new BusinessException(AuthErrorCode.TOKEN_INVALID);
			}
		}

		// 3. SecurityUser로 UserDetails 생성 → UsernamePasswordAuthenticationToken 만들고
		// SecurityContextHolder.getContext().setAuthentication(authentication)로 현재 요청의 인증 완료.
		UserDetails userDetails = new SecurityUser(
			user.getId(),
			user.getPassword(),
			user.getNickname(),
			List.of(new SimpleGrantedAuthority("ROLE_USER"))
		);

		Authentication authentication = new UsernamePasswordAuthenticationToken(
			userDetails,
			userDetails.getPassword(),
			userDetails.getAuthorities()
		);

		SecurityContextHolder
			.getContext()
			.setAuthentication(authentication);

		filterChain.doFilter(request, response);
	}
}
