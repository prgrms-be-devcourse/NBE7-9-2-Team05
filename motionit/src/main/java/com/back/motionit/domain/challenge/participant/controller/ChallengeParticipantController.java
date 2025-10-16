package com.back.motionit.domain.challenge.participant.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.participant.service.ChallengeParticipantService;
import com.back.motionit.global.respoonsedata.ResponseData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/challenge/participants")
@RequiredArgsConstructor
@Tag(name = "ApiV1ChallengeParticipantController", description = "챌린지 참여 API")
public class ChallengeParticipantController {

	private final ChallengeParticipantService challengeParticipantService;

	@PostMapping("/{roomId}/join")
	@Operation(summary = "챌린지 참여")
	public ResponseData<Void> joinChallengeRoom(
		@PathVariable Long roomId,
		@RequestParam Long userId
	) {
		challengeParticipantService.joinChallengeRoom(userId, roomId);
		return new ResponseData<>(
			"200",
			"챌린지 룸에 참가되었습니다."
		);
	}

	@PostMapping("/{roomId}/leave")
	@Operation(summary = "챌린지 탈퇴")
	public ResponseData<Void> leaveChallengeRoom(
		@PathVariable Long roomId,
		@RequestParam Long userId
	) {
		challengeParticipantService.leaveChallenge(userId, roomId);
		return new ResponseData<>(
			"200",
			"챌린지 룸에서 나갔습니다."
		);
	}
}
