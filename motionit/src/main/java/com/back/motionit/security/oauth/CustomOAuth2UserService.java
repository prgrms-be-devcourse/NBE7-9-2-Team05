package com.back.motionit.security.oauth;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.auth.social.service.SocialAuthService;
import com.back.motionit.domain.user.entity.LoginType;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.security.SecurityUser;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

	private final SocialAuthService socialAuthService;

	// 카카오 회원 정보 수정시 db 반영을 위한 더티체킹
	@Transactional
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

		OAuth2User oAuth2User = super.loadUser(userRequest);

		Long kakaoId = Long.parseLong(oAuth2User.getName());

		Map<String, Object> attributes = oAuth2User.getAttributes();
		Map<String, Object> attributesProperties = (Map<String, Object>)attributes.get("properties");

		String nickname = (String)attributesProperties.get("nickname");
		String userProfile = (String)attributesProperties.get("profile_image");
		String password = "";
		String email = "";
		LoginType loginType = LoginType.KAKAO;

		User user = socialAuthService.modifyOrJoin(kakaoId, email, nickname, password, loginType, userProfile);

		// 최소 권한 부여
		Collection<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

		return new SecurityUser(
			user.getId(),
			user.getPassword(),
			user.getNickname(),
			authorities
		);
	}
}
