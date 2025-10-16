package com.back.motionit.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeRoomErrorCode implements ErrorCode {
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, "R-001", "유저를 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}