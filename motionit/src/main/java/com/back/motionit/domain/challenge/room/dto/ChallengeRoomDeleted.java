package com.back.motionit.domain.challenge.room.dto;

import com.back.motionit.global.enums.EventEnums;

public record ChallengeRoomDeleted(
	String event
) {
	public ChallengeRoomDeleted(EventEnums event) {
		this(event.getEvent());
	}
}
