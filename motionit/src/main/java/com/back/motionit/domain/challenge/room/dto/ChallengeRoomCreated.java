package com.back.motionit.domain.challenge.room.dto;

import com.back.motionit.global.enums.EventEnums;

public record ChallengeRoomCreated(
	String event,
	Long roomId
) {
	public ChallengeRoomCreated(EventEnums event, Long roomId) {
		this(event.getEvent(), roomId);
	}
}
