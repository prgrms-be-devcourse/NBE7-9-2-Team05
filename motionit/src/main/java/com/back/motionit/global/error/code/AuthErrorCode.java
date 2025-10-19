package com.back.motionit.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

	EMAIL_DUPLICATED(HttpStatus.CONFLICT, "U-100", "이미 사용중인 이메일입니다."),
	LOGIN_FAILED(HttpStatus.UNAUTHORIZED, "U-101", "이메일 또는 비밀번호가 일치하지 않습니다."),
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U-102", "사용자를 찾을 수 없습니다."),
	//
	NICKNAME_DUPLICATED(HttpStatus.CONFLICT, "U-103", "이미 사용중인 닉네임입니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
