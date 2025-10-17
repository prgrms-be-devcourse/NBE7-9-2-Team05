package com.back.motionit.global.error.code;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChallengeVideoErrorCode implements ErrorCode {
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, "R-600", "유저를 찾을 수 없습니다."),
	CANNOT_FIND_CHALLENGE_ROOM(HttpStatus.NOT_FOUND, "R-601", "챌린지 룸을 찾을 수 없습니다."),
	INVALID_VIDEO_FORMAT(HttpStatus.BAD_REQUEST, "R-602", "유효하지 않은 비디오 형식입니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;
}
