package com.back.motionit.domain.challenge.mission.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.mission.dto.ChallengeMissionCompleteRequest;
import com.back.motionit.domain.challenge.mission.dto.ChallengeMissionStatusResponse;
import com.back.motionit.domain.challenge.mission.entity.ChallengeMissionStatus;
import com.back.motionit.domain.challenge.mission.service.ChallengeMissionStatusService;
import com.back.motionit.global.respoonsedata.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/challenge/rooms/{roomId}/missions")
@RequiredArgsConstructor
public class ChallengeMissionStatusController {

	private final ChallengeMissionStatusService challengeMissionStatusService;

	@PostMapping("/complete")
	public ResponseData<ChallengeMissionStatusResponse> completeMission(
		@PathVariable Long roomId,
		@Valid @RequestBody ChallengeMissionCompleteRequest request
	) {
		ChallengeMissionStatus mission = challengeMissionStatusService.completeMission(
			roomId, request.participantId(), request.videoId()
		);

		return ResponseData.success("미션 완료 처리 성공", ChallengeMissionStatusResponse.from(mission));
	}

	@GetMapping("/today")
	public ResponseData<List<ChallengeMissionStatusResponse>> getTodayMissionStatus(
		@PathVariable Long roomId,
		@RequestBody ChallengeMissionCompleteRequest request
	) {
		List<ChallengeMissionStatusResponse> list = challengeMissionStatusService
			.getTodayMissionsByRoom(roomId)
			.stream()
			.map(ChallengeMissionStatusResponse::from)
			.toList();
		return ResponseData.success("오늘의 방 미션 현황 조회 성공", list);
	}

	@GetMapping("/{participantId}/today")
	public ResponseData<ChallengeMissionStatusResponse> getTodayMissionStatus(
		@PathVariable Long roomId,
		@PathVariable Long participantId
	) {
		ChallengeMissionStatus mission = challengeMissionStatusService.getTodayMissionStatus(roomId, participantId);
		return ResponseData.success("참여자 미션 상태 조회 성공", ChallengeMissionStatusResponse.from(mission));
	}

	@GetMapping("/{participantId}/history")
	public ResponseData<List<ChallengeMissionStatusResponse>> getMissionHistory(
		@PathVariable Long roomId,
		@PathVariable Long participantId
	) {
		List<ChallengeMissionStatusResponse> list = challengeMissionStatusService
			.getMissionHistory(roomId, participantId)
			.stream()
			.map(ChallengeMissionStatusResponse::from)
			.toList();

		return ResponseData.success("참여자 미션 기록 조회 성공", list);
	}
}
