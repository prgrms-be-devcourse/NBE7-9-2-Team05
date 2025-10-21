package com.back.motionit.domain.challenge.room.api;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.back.motionit.domain.challenge.room.api.response.ChallengeRoomHttp;
import com.back.motionit.domain.challenge.room.dto.CreateRoomRequest;
import com.back.motionit.domain.challenge.room.dto.CreateRoomResponse;
import com.back.motionit.global.respoonsedata.ResponseData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "운동방", description = "Controller to handle challenge room API")
public interface ChallengeRoomApi {
	@PostMapping
	@Operation(summary = "Create Challenge Room", description = "운동방 생성 요청값을 전달받아 운동방을 생성합니다.")
	@ApiResponses({
		@ApiResponse(responseCode = ChallengeRoomHttp.CREATE_ROOM_SUCCESS_CODE,
			description = ChallengeRoomHttp.CREATE_ROOM_SUCCESS_MESSAGE,
			content = @Content(schema = @Schema(implementation = CreateRoomResponse.class)))
	})
	ResponseData<CreateRoomResponse> createRoom(@RequestBody @Valid CreateRoomRequest request);
}
