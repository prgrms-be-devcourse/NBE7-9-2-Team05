package com.back.motionit.domain.auth.social;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.back.motionit.domain.user.entity.LoginType;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.error.code.AuthErrorCode;
import com.back.motionit.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocialAuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

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
			return join(kakaoId, email, nickname, passwordEncoder.encode(password), loginType, userProfile);
		}

		user.update(nickname, userProfile);

		return user;
	}
}
