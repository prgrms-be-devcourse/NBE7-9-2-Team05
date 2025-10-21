package com.back.motionit.domain.challenge.room.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.room.api.ChallengeRoomApi;
import com.back.motionit.domain.challenge.room.api.response.ChallengeRoomHttp;
import com.back.motionit.domain.challenge.room.dto.CreateRoomRequest;
import com.back.motionit.domain.challenge.room.dto.CreateRoomResponse;
import com.back.motionit.domain.challenge.room.service.ChallengeRoomService;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.request.RequestContext;
import com.back.motionit.global.respoonsedata.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/challenge/rooms")
@Validated
public class ChallengeRoomController implements ChallengeRoomApi {

	private final ChallengeRoomService challengeRoomService;

	private final RequestContext httpRequest;

	@Override
	public ResponseData<CreateRoomResponse> createRoom(@RequestBody @Valid CreateRoomRequest request) {
		User user = httpRequest.getActor();
		CreateRoomResponse response = challengeRoomService.createRoom(request, user);
		return ResponseData.success(ChallengeRoomHttp.CREATE_ROOM_SUCCESS_CODE,
			ChallengeRoomHttp.CREATE_ROOM_SUCCESS_MESSAGE, response);
	}
}
