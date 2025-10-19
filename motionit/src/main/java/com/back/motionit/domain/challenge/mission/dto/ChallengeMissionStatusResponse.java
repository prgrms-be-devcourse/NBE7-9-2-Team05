package com.back.motionit.domain.challenge.mission.dto;

import java.time.LocalDate;

import com.back.motionit.domain.challenge.mission.entity.ChallengeMissionStatus;

public record ChallengeMissionStatusResponse(
	Long participantId,
	Long videoId,
	LocalDate missionDate,
	boolean completed
) {
	public static ChallengeMissionStatusResponse from(ChallengeMissionStatus status) {
		return new ChallengeMissionStatusResponse(
			status.getParticipant().getId(),
			status.getVideo() != null ? status.getVideo().getId() : null,
			status.getMissionDate(),
			status.getCompleted()
		);
	}
}
