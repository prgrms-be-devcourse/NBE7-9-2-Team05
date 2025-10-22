package com.back.motionit.domain.challenge.mission.controller;

import static com.back.motionit.domain.challenge.mission.api.response.ChallengeMissionStatusHttp.*;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.mission.api.ChallengeMissionStatusApi;
import com.back.motionit.domain.challenge.mission.dto.ChallengeMissionCompleteRequest;
import com.back.motionit.domain.challenge.mission.dto.ChallengeMissionStatusResponse;
import com.back.motionit.domain.challenge.mission.entity.ChallengeMissionStatus;
import com.back.motionit.domain.challenge.mission.service.ChallengeMissionStatusService;
import com.back.motionit.global.request.RequestContext;
import com.back.motionit.global.respoonsedata.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/challenge/rooms/{roomId}/missions")
@RequiredArgsConstructor
public class ChallengeMissionStatusController implements ChallengeMissionStatusApi {

	private final ChallengeMissionStatusService challengeMissionStatusService;
	private final RequestContext requestContext;

	@PostMapping("/complete")
	public ResponseData<ChallengeMissionStatusResponse> completeMission(
		@PathVariable Long roomId,
		@Valid @RequestBody ChallengeMissionCompleteRequest request
	) {
		// User actor = requestContext.getActor();
		Long mockUserId = 7L;

		ChallengeMissionStatus mission = challengeMissionStatusService.completeMission(
			roomId, mockUserId, request.videoId()
		);

		return ResponseData.success(MISSION_COMPLETE_SUCCESS_MESSAGE, ChallengeMissionStatusResponse.from(mission));
	}

	@GetMapping("/today")
	public ResponseData<List<ChallengeMissionStatusResponse>> getTodayMissionByRoom(
		@PathVariable Long roomId
	) {
		// User actor = requestContext.getActor();
		Long mockUserId = 7L;

		List<ChallengeMissionStatusResponse> list = challengeMissionStatusService
			.getTodayMissionsByRoom(roomId, mockUserId)
			.stream()
			.map(ChallengeMissionStatusResponse::from)
			.toList();

		if (list.isEmpty()) {
			return ResponseData.success(GET_TODAY_NO_MISSION_MESSAGE, list);
		}
		return ResponseData.success(GET_TODAY_SUCCESS_MESSAGE, list);
	}

	@GetMapping("/{participantId}/today")
	public ResponseData<ChallengeMissionStatusResponse> getTodayMissionStatus(
		@PathVariable Long roomId
	) {
		// User actor = requestContext.getActor();
		Long mockUserId = 7L;

		ChallengeMissionStatus mission = challengeMissionStatusService.getTodayMissionStatus(roomId, mockUserId);
		return ResponseData.success(GET_TODAY_PARTICIPANT_SUCCESS_MESSAGE,
			ChallengeMissionStatusResponse.from(mission));
	}

	@GetMapping("/{participantId}/history")
	public ResponseData<List<ChallengeMissionStatusResponse>> getMissionHistory(
		@PathVariable Long roomId
	) {
		// User actor = requestContext.getActor();
		Long mockUserId = 7L;

		List<ChallengeMissionStatusResponse> list = challengeMissionStatusService
			.getMissionHistory(roomId, mockUserId)
			.stream()
			.map(ChallengeMissionStatusResponse::from)
			.toList();

		return ResponseData.success(GET_MISSION_HISTORY_SUCCESS_MESSAGE, list);
	}
}
