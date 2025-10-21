package com.back.motionit.domain.challenge.room.dto;

import com.back.motionit.global.enums.EventEnums;

public record ChallengeRoomCreated(
	EventEnums event,
	Long roomId
) {
}
