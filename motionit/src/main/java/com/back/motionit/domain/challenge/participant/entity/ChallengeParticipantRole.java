package com.back.motionit.domain.challenge.participant.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChallengeParticipantRole {
	ADMIN("admin"),
	NORMAL("normal");

	private final String value;
}
