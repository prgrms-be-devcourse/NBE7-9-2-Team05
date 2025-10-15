package com.back.motionit.domain.challenge.room.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.room.constant.ChallengeRoomConstant;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.helper.UserHelper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class ChallengeRoomControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ChallengeRoomRepository challengeRoomRepository;

	@Autowired
	private UserHelper userHelper;
	private Long roomId;
	private User user;

	@BeforeEach
	public void setUp() {
		roomId = 3L;
		user = userHelper.createUser();
	}

	@Test
	@DisplayName("POST `/api/v1/rooms` - Create Challenge Room")
	void createRoomTest() throws Exception {
		ResultActions resultActions = mvc.perform(
			post("/api/v1/rooms")
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		ChallengeRoom createdRoom = challengeRoomRepository.findById(roomId)
			.orElseThrow(() -> new IllegalArgumentException("Challenge Room not found with id: %s".formatted(roomId)));

		resultActions
			.andExpect(handler().handlerType(ChallengeRoomController.class))
			.andExpect(handler().methodName("createRoom"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.resultCode").value(ChallengeRoomConstant.CREATE_ROOM_SUCCESS_CODE))
			.andExpect(jsonPath("$.msg").value(ChallengeRoomConstant.CREATE_ROOM_SUCCESS_MESSAGE))
			.andExpect(jsonPath("$.data.id").value(createdRoom.getId()));
	}
}
