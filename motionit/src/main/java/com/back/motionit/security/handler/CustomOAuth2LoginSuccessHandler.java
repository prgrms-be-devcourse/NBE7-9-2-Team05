package com.back.motionit.security.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.back.motionit.domain.auth.social.service.SocialAuthService;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.rq.Rq;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CustomOAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

	private final SocialAuthService socialAuthService;
	private final Rq rq;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
		Authentication authentication) throws IOException {

		User user = rq.getActor();
		String accessToken = socialAuthService.generateAccessToken(user);
		String refreshToken = socialAuthService.generateRefreshToken(user);

		socialAuthService.saveRefreshToken(user.getId(), refreshToken);

		rq.setHeader("accessToken", accessToken);
		rq.setCookie("refreshToken", refreshToken);

		rq.sendRedirect("http://localhost:3000");
	}
}
