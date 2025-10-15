package com.back.motionit.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import com.back.motionit.domain.user.entity.LoginType;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

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

	@CreatedDate
	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@LastModifiedDate
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

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
