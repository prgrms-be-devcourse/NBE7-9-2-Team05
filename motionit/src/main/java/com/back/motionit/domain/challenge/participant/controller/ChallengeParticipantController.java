package com.back.motionit.domain.challenge.participant.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.participant.api.ChallengeParticipantApi;
import com.back.motionit.domain.challenge.participant.api.response.ChallengeParticipantHttp;
import com.back.motionit.domain.challenge.participant.service.ChallengeParticipantService;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.request.RequestContext;
import com.back.motionit.global.respoonsedata.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/challenge/participants")
@RequiredArgsConstructor
public class ChallengeParticipantController implements ChallengeParticipantApi {

	private final ChallengeParticipantService challengeParticipantService;
	private final RequestContext requestContext;

	@Override
	public ResponseData<Void> joinChallengeRoom(Long roomId) {
		User actor = requestContext.getActor();
		challengeParticipantService.joinChallengeRoom(actor.getId(), roomId);
		return ResponseData.success(ChallengeParticipantHttp.JOIN_SUCCESS_MESSAGE, null);
	}

	@Override
	public ResponseData<Void> leaveChallengeRoom(Long roomId) {
		User actor = requestContext.getActor();
		challengeParticipantService.leaveChallenge(actor.getId(), roomId);
		return ResponseData.success(ChallengeParticipantHttp.LEAVE_SUCCESS_MESSAGE, null);
	}
}
