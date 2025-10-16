package com.back.motionit.domain.auth.local.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.auth.dto.AuthResponse;
import com.back.motionit.domain.auth.dto.LoginRequest;
import com.back.motionit.domain.auth.dto.SignupRequest;
import com.back.motionit.domain.auth.local.service.LocalAuthService;
import com.back.motionit.global.respoonsedata.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/auth/local")
@RequiredArgsConstructor
@Validated
public class LocalAuthController {

	private final LocalAuthService localAuthService;

	@PostMapping("/signup")
	public ResponseEntity<ResponseData<AuthResponse>> signup(
		@Valid @RequestBody SignupRequest request) {

		AuthResponse response = localAuthService.signup(request);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(new ResponseData<>("201", "회원가입이 완료되었습니다.", response));
	}

	@PostMapping("/login")
	public ResponseEntity<ResponseData<AuthResponse>> login(
		@Valid @RequestBody LoginRequest request) {

		AuthResponse response = localAuthService.login(request);

		return ResponseEntity
			.ok(new ResponseData<>("200", "로그인이 완료되었습니다.", response));
	}

	@PostMapping("/logout")
	public ResponseEntity<ResponseData<Void>> logout(@RequestParam Long userId) {
		// TODO: JWT 인증 완성 후 @AuthenticationPrincipal로 userId 받기

		localAuthService.logout(userId);

		return ResponseEntity
			.ok(new ResponseData<>("200", "로그아웃이 완료되었습니다."));
	}
}
