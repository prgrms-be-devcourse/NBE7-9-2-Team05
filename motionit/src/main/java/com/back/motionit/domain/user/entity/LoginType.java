package com.back.motionit.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum LoginType {
	KAKAO("Kakao"),
	LOCAL("Local");

	private final String value;
}
