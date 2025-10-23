package com.back.motionit.domain.challenge.mission.controller;

import static com.back.motionit.domain.challenge.mission.api.response.ChallengeMissionStatusHttp.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.mission.api.ChallengeMissionStatusApi;
import com.back.motionit.domain.challenge.mission.dto.ChallengeMissionStatusResponse;
import com.back.motionit.domain.challenge.mission.entity.ChallengeMissionStatus;
import com.back.motionit.domain.challenge.mission.service.ChallengeMissionStatusService;
import com.back.motionit.domain.challenge.validator.ChallengeAuthValidator;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.request.RequestContext;
import com.back.motionit.global.respoonsedata.ResponseData;
import com.back.motionit.global.service.GptService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/challenge/rooms/{roomId}/missions")
@RequiredArgsConstructor
public class ChallengeMissionStatusController implements ChallengeMissionStatusApi {

	private final ChallengeMissionStatusService challengeMissionStatusService;
	private final RequestContext requestContext;
	private final ChallengeAuthValidator challengeAuthValidator; // 챌린지 방참여자 여부 판단
	private final GptService gptService;

	@PostMapping("/complete")
	public ResponseData<ChallengeMissionStatusResponse> completeMission(
		@PathVariable Long roomId
	) {
		User actor = requestContext.getActor();
		// 방 참여자가 아닐경우 API 접근 차단
		challengeAuthValidator.validateActiveParticipant(actor.getId(), roomId);

		ChallengeMissionStatus mission = challengeMissionStatusService.completeMission(
			roomId, actor.getId()
		);

		String aiSummary = null;
		try {
			aiSummary = gptService.generateMissionCompleteSummary(
				actor.getNickname(),
				mission.getParticipant().getChallengeRoom().getTitle()
			);
		} catch (Exception e) {
			// AI 생성 실패는 로그만 남기고 계속 진행
			log.warn("[Mission Complete] AI summary generation failed for user: {}, room: {}", actor.getId(), roomId,
				e);
		}

		return ResponseData.success(
			MISSION_COMPLETE_SUCCESS_MESSAGE,
			ChallengeMissionStatusResponse.from(mission, aiSummary)
		);
	}

	@GetMapping("/today")
	public ResponseData<List<ChallengeMissionStatusResponse>> getTodayMissionByRoom(
		@PathVariable Long roomId
	) {
		User actor = requestContext.getActor();
		// 방 참여자가 아닐경우 API 접근 차단
		challengeAuthValidator.validateActiveParticipant(actor.getId(), roomId);

		List<ChallengeMissionStatusResponse> list = challengeMissionStatusService
			.getTodayMissionsByRoom(roomId, actor.getId())
			.stream()
			.map(ChallengeMissionStatusResponse::from)
			.toList();

		if (list.isEmpty()) {
			return ResponseData.success(GET_TODAY_NO_MISSION_MESSAGE, list);
		}
		return ResponseData.success(GET_TODAY_SUCCESS_MESSAGE, list);
	}

	@GetMapping("/personal/today")
	public ResponseData<ChallengeMissionStatusResponse> getTodayMissionStatus(
		@PathVariable Long roomId
	) {
		User actor = requestContext.getActor();
		// 방 참여자가 아닐경우 API 접근 차단
		challengeAuthValidator.validateActiveParticipant(actor.getId(), roomId);

		ChallengeMissionStatus mission = challengeMissionStatusService.getTodayMissionStatus(roomId, actor.getId());
		return ResponseData.success(GET_TODAY_PARTICIPANT_SUCCESS_MESSAGE,
			ChallengeMissionStatusResponse.from(mission));
	}

	@GetMapping("/personal/history")
	public ResponseData<List<ChallengeMissionStatusResponse>> getMissionHistory(
		@PathVariable Long roomId
	) {
		User actor = requestContext.getActor();
		// 방 참여자가 아닐경우 API 접근 차단
		challengeAuthValidator.validateActiveParticipant(actor.getId(), roomId);

		List<ChallengeMissionStatusResponse> list = challengeMissionStatusService
			.getMissionHistory(roomId, actor.getId())
			.stream()
			.map(ChallengeMissionStatusResponse::from)
			.toList();

		return ResponseData.success(GET_MISSION_HISTORY_SUCCESS_MESSAGE, list);
	}
}
