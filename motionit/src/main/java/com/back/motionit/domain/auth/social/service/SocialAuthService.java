package com.back.motionit.domain.auth.social.service;

import java.util.Map;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.user.entity.LoginType;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.error.code.AuthErrorCode;
import com.back.motionit.global.error.exception.BusinessException;
import com.back.motionit.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtTokenProvider jwtTokenProvider;

	public User join(Long kakaoId, String email, String nickname, String password, LoginType loginType,
		String userProfile) {

		userRepository.findByNickname(nickname)
			.ifPresent(m -> {
				throw new BusinessException(AuthErrorCode.NICKNAME_DUPLICATED);
			});

		User user = new User(kakaoId, email, nickname, passwordEncoder.encode(password), loginType, userProfile);
		return userRepository.save(user);
	}

	// 회원가입, 카카오 정보 최신 상태 반영
	public User modifyOrJoin(Long kakaoId, String email, String nickname, String password, LoginType loginType,
		String userProfile) {

		User user = userRepository.findByNickname(nickname).orElse(null);

		if (user == null) {
			return join(kakaoId, email, nickname, password, loginType, userProfile);
		}

		user.update(nickname, userProfile);

		return user;
	}

	public String generateAccessToken(User user) {
		return jwtTokenProvider.generateAccessToken(user);
	}

	public String generateRefreshToken(User user) {
		return jwtTokenProvider.generateRefreshToken(user);
	}

	@Transactional
	public void saveRefreshToken(Long userId, String refreshToken) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(AuthErrorCode.USER_NOT_FOUND));
		user.updateRefreshToken(refreshToken);
	}

	public Map<String, Object> payloadOrNull(String accessToken) {
		return jwtTokenProvider.payloadOrNull(accessToken);
	}
}
