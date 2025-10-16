package com.back.motionit.domain.challenge.room.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.back.motionit.domain.challenge.room.api.ChallengeRoomApi;
import com.back.motionit.domain.challenge.room.api.response.ChallengeRoomHttp;
import com.back.motionit.domain.challenge.room.dto.CreateRoomRequest;
import com.back.motionit.domain.challenge.room.dto.CreateRoomResponse;
import com.back.motionit.domain.challenge.room.service.ChallengeRoomService;
import com.back.motionit.global.respoonsedata.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenge/rooms")
@Validated
public class ChallengeRoomController implements ChallengeRoomApi {

	private final ChallengeRoomService challengeRoomService;

	@Override
	public ResponseData<CreateRoomResponse> createRoom(@RequestPart("request") @Valid CreateRoomRequest request,
		@RequestPart(name = "image", required = false) MultipartFile image) {
		CreateRoomResponse response = challengeRoomService.createRoom(request, image);
		return ResponseData.success(ChallengeRoomHttp.CREATE_ROOM_SUCCESS_CODE,
			ChallengeRoomHttp.CREATE_ROOM_SUCCESS_MESSAGE, response);
	}
}
