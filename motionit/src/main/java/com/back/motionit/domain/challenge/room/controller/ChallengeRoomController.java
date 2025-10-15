package com.back.motionit.domain.challenge.room.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.room.constant.ChallengeRoomConstant;
import com.back.motionit.domain.challenge.room.dto.CreateRoomResponse;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.service.ChallengeRoomService;
import com.back.motionit.global.respoonsedata.ResponseData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rooms")
@Tag(name = "ChallengeRoomController", description = "Controller to handle challenge room API")
public class ChallengeRoomController {

	private final ChallengeRoomService challengeRoomService;

	@PostMapping
	@Operation(summary = "Create Challenge Room")
	public ResponseData<CreateRoomResponse> createRoom() {
		ChallengeRoom room = challengeRoomService.createRoom();
		return new ResponseData(
			ChallengeRoomConstant.CREATE_ROOM_SUCCESS_CODE,
			ChallengeRoomConstant.CREATE_ROOM_SUCCESS_MESSAGE,
			new CreateRoomResponse(room)
		);
	}
}
