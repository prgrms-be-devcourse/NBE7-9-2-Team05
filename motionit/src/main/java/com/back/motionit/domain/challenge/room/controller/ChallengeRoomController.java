package com.back.motionit.domain.challenge.room.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.back.motionit.domain.challenge.room.constant.ChallengeRoomHttp;
import com.back.motionit.domain.challenge.room.dto.CreateRoomRequest;
import com.back.motionit.domain.challenge.room.dto.CreateRoomResponse;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.service.ChallengeRoomService;
import com.back.motionit.global.respoonsedata.ResponseData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenge/rooms")
@Tag(name = "ChallengeRoomController", description = "Controller to handle challenge room API")
public class ChallengeRoomController {

	private final ChallengeRoomService challengeRoomService;

	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@Operation(summary = "Create Challenge Room")
	public ResponseData<CreateRoomResponse> createRoom(@RequestPart("request") @Valid CreateRoomRequest request,
		@RequestPart(name = "image", required = false) MultipartFile image) {
		ChallengeRoom room = challengeRoomService.createRoom(request, image);
		return new ResponseData<>(
			ChallengeRoomHttp.CREATE_ROOM_SUCCESS_CODE,
			ChallengeRoomHttp.CREATE_ROOM_SUCCESS_MESSAGE,
			new CreateRoomResponse(room)
		);
	}
}
