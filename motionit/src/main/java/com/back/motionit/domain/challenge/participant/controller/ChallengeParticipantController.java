package com.back.motionit.domain.challenge.participant.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.participant.api.ChallengeParticipantApi;
import com.back.motionit.domain.challenge.participant.service.ChallengeParticipantService;
import com.back.motionit.global.respoonsedata.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/challenge/participants")
@RequiredArgsConstructor
public class ChallengeParticipantController implements ChallengeParticipantApi {

	private final ChallengeParticipantService challengeParticipantService;

	@Override
	public ResponseData<Void> joinChallengeRoom(Long roomId, Long userId) {
		challengeParticipantService.joinChallengeRoom(userId, roomId);
		return ResponseData.success("운동방에 참가했습니다.", null);
	}

	@Override
	public ResponseData<Void> leaveChallengeRoom(Long roomId, Long userId) {
		challengeParticipantService.leaveChallenge(userId, roomId);
		return ResponseData.success("운동방에서 탈퇴했습니다.", null);
	}
}
