package com.back.motionit.domain.user.entity;

import com.back.motionit.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "users")
public class User extends BaseEntity {

	@Column(unique = true)
	private String email;

	@Column(name = "kakao_id")
	private String kakaoId;

	private String password;

	private String nickname;

	@Column(name = "refresh_token", length = 512)
	private String refreshToken;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, name = "login_type")
	private LoginType loginType = LoginType.LOCAL;

	@Column(name = "user_profile")
	private String userProfile; // 사용자 프로필 이미지 URL
}
