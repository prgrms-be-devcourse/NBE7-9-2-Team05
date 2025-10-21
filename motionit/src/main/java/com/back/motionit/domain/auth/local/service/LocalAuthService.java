package com.back.motionit.domain.auth.local.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.auth.dto.AuthResponse;
import com.back.motionit.domain.auth.dto.LoginRequest;
import com.back.motionit.domain.auth.dto.SignupRequest;
import com.back.motionit.domain.user.entity.LoginType;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.constants.ProfileImageConstants;
import com.back.motionit.global.error.code.AuthErrorCode;
import com.back.motionit.global.error.exception.BusinessException;
import com.back.motionit.global.request.RequestContext;
import com.back.motionit.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalAuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;
	private final RequestContext requestContext;

	@Transactional
	public AuthResponse signup(SignupRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException(AuthErrorCode.EMAIL_DUPLICATED);
		}

		if (userRepository.existsByNickname(request.getNickname())) {
			throw new BusinessException(AuthErrorCode.NICKNAME_DUPLICATED);
		}

		String encodedPassword = passwordEncoder.encode(request.getPassword());

		User user = User.builder()
			.email(request.getEmail())
			.password(encodedPassword)
			.nickname(request.getNickname())
			.loginType(LoginType.LOCAL)
			.userProfile(ProfileImageConstants.DEFAULT_PROFILE_IMAGE)
			.build();

		User savedUser = userRepository.save(user);

		String accessToken = jwtTokenProvider.generateAccessToken(savedUser);
		String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser);

		savedUser.updateRefreshToken(refreshToken);

		return AuthResponse.builder()
			.userId(savedUser.getId())
			.email(savedUser.getEmail())
			.nickname(savedUser.getNickname())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Transactional
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new BusinessException(AuthErrorCode.LOGIN_FAILED));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BusinessException(AuthErrorCode.LOGIN_FAILED);
		}

		String accessToken = jwtTokenProvider.generateAccessToken(user);
		String refreshToken = jwtTokenProvider.generateRefreshToken(user);

		requestContext.setCookie("accessToken", accessToken);
		requestContext.setCookie("refreshToken", refreshToken);

		user.updateRefreshToken(refreshToken);

		return AuthResponse.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Transactional
	public void logout(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

		user.removeRefreshToken();
	}
}
