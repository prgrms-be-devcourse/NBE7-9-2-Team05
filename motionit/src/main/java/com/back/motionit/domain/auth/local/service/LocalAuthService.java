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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocalAuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	// TODO: JWT 팀원 완성 후 추가
	// private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public AuthResponse signup(SignupRequest request) {
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new BusinessException(AuthErrorCode.EMAIL_DUPLICATED);
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

		// TODO: JWT 토큰 생성
		// JwtTokenDto tokenDto = jwtTokenProvider.generateToken(authentication);

		return AuthResponse.builder()
			.userId(savedUser.getId())
			.email(savedUser.getEmail())
			.nickname(savedUser.getNickname())
			.build();
	}

	@Transactional
	public AuthResponse login(LoginRequest request) {
		User user = userRepository.findByEmail(request.getEmail())
			.orElseThrow(() -> new BusinessException(AuthErrorCode.LOGIN_FAILED));

		if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
			throw new BusinessException(AuthErrorCode.LOGIN_FAILED);
		}

		// TODO: JWT 토큰 생성
		// TODO: Refresh Token DB 저장

		return AuthResponse.builder()
			.userId(user.getId())
			.email(user.getEmail())
			.nickname(user.getNickname())
			.build();
	}

	@Transactional
	public void logout(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));

		user.removeRefreshToken();
	}
}
