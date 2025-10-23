package com.back.motionit.domain.challenge.mission.dto;

import java.time.LocalDate;

import org.springframework.lang.Nullable;

import com.back.motionit.domain.challenge.mission.entity.ChallengeMissionStatus;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipantRole;

public record ChallengeMissionStatusResponse(
	Long participantId,
	LocalDate missionDate,
	boolean completed,
	ChallengeParticipantRole isHost,
	@Nullable String aiSummary
) {
	public static ChallengeMissionStatusResponse from(ChallengeMissionStatus status) {
		return new ChallengeMissionStatusResponse(
			status.getParticipant().getId(),
			status.getMissionDate(),
			status.getCompleted(),
			status.getParticipant().getRole(),
			null
		);
	}
	
	public static ChallengeMissionStatusResponse from(ChallengeMissionStatus status, String aiSummary) {
		return new ChallengeMissionStatusResponse(
			status.getParticipant().getId(),
			status.getMissionDate(),
			status.getCompleted(),
			status.getParticipant().getRole(),
			aiSummary
		);
	}
}
