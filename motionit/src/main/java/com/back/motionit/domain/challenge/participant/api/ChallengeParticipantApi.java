package com.back.motionit.domain.challenge.participant.api;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.back.motionit.global.respoonsedata.ResponseData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;

@Tag(name = "ApiV1ChallengeParticipantController", description = "운동방 참여 API")
public interface ChallengeParticipantApi {

	@PostMapping("/{roomId}/join")
	@Operation(
		summary = "운동방 참가",
		description = "운동방에 참가합니다. 이미 참가한 유저는 중복 참가할 수 없습니다. 정원이 초과된 방에는 참가할 수 없습니다.",
		responses = {
			@ApiResponse(responseCode = "201", description = "정상적으로 참가 완료"),
			@ApiResponse(responseCode = "400", description = "이미 참가한 유저 / 정원 초과"),
			@ApiResponse(responseCode = "404", description = "존재하지 않는 방 또는 유저")
		})
	ResponseData<Void> joinChallengeRoom(
		@PathVariable @NotNull Long roomId,
		@RequestParam @NotNull Long userId
	);

	@PostMapping("/{roomId}/leave")
	@Operation(summary = "챌린지 탈퇴",
		description = "챌린지에서 탈퇴합니다. 참가하지 않은 유저는 탈퇴할 수 없습니다.",
		responses = {
			@ApiResponse(responseCode = "200", description = "정상적으로 탈퇴 완료"),
			@ApiResponse(responseCode = "400", description = "참가하지 않은 유저"),
			@ApiResponse(responseCode = "404", description = "존재하지 않는 방 또는 유저")
		})
	ResponseData<Void> leaveChallengeRoom(
		@PathVariable @NotNull Long roomId,
		@RequestParam @NotNull Long userId
	);
}
