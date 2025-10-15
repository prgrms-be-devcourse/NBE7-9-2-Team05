package com.back.motionit.domain.user.entity;

import com.back.motionit.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Column(name = "kakao_id")
	private Long kakaoId;

	@Column(nullable = false, unique = true, length = 100)
	private String email;

	@Column(nullable = false, length = 50)
	private String nickname;

	@Column(length = 255)
	private String password;

	@Column(name = "refresh_token", length = 500)
	private String refreshToken;

	@Enumerated(EnumType.STRING)
	@Column(name = "login_type", nullable = false, length = 20)
	private LoginType loginType;

	@Column(name = "user_profile", length = 500)
	private String userProfile;

	@Builder
	public User(Long kakaoId, String email, String nickname, String password,
		LoginType loginType, String userProfile) {
		this.kakaoId = kakaoId;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.loginType = loginType;
		this.userProfile = userProfile;
	}

	public void updateRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public void removeRefreshToken() {
		this.refreshToken = null;
	}

	public void updatePassword(String password) {
		this.password = password;
	}
}
