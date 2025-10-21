package com.back.motionit.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.error.code.AuthErrorCode;
import com.back.motionit.global.error.exception.BusinessException;
import com.back.motionit.security.jwt.JwtTokenDto;
import com.back.motionit.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthTokenService {

	private final JwtTokenProvider jwtTokenProvider;
	private final UserRepository userRepository;

	@Transactional
	public JwtTokenDto generateTokens(User user) {
		String accessToken = jwtTokenProvider.generateAccessToken(user);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user);

		user.updateRefreshToken(refreshToken);

		return JwtTokenDto.builder()
			.grantType("Bearer")
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.accessTokenExpiresIn(System.currentTimeMillis())
			.build();
	}

	@Transactional
	public void removeRefreshToken(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));
		user.removeRefreshToken();
	}
}

