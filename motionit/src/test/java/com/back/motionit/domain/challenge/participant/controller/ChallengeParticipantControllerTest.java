package com.back.motionit.domain.challenge.participant.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

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

import com.back.motionit.domain.challenge.participant.api.response.ChallengeParticipantHttp;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipantRole;
import com.back.motionit.domain.challenge.participant.repository.ChallengeParticipantRepository;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.factory.ChallengeRoomFactory;
import com.back.motionit.global.error.code.ChallengeParticipantErrorCode;
import com.back.motionit.helper.UserHelper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class ChallengeParticipantControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ChallengeParticipantRepository challengeParticipantRepository;

	@Autowired
	private ChallengeRoomRepository challengeRoomRepository;

	@Autowired
	private UserHelper userHelper;

	private User user;
	private ChallengeRoom room;

	@BeforeEach
	void setUp() {
		user = userHelper.createUser();
		room = createTestRoom(user);
	}

	private ChallengeRoom createTestRoom(User owner) {
		ChallengeRoom room = ChallengeRoomFactory.fakeChallengeRoom(owner);
		return challengeRoomRepository.save(room);
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/participants/{roomId}/join` - Success Join Challenge Room")
	void successJoinChallengeRoom() throws Exception {
		ResultActions resultActions = mvc.perform(
			post("/api/v1/challenge/participants/{roomId}/join", room.getId())
				.param("userId", user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		resultActions
			.andExpect(handler().handlerType(ChallengeParticipantController.class))
			.andExpect(handler().methodName("joinChallengeRoom"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value(ChallengeParticipantHttp.JOIN_SUCCESS_CODE))
			.andExpect(jsonPath("$.msg").value(ChallengeParticipantHttp.JOIN_SUCCESS_MESSAGE));

		Optional<ChallengeParticipant> participant =
			challengeParticipantRepository.findByUserAndChallengeRoom(user, room);

		assertThat(participant).isPresent();
		assertThat(participant.get().getQuited()).isFalse();
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/participants/{roomId}/join` - Fail when Already Joined")
	void failWhenAlreadyJoined() throws Exception {
		// given - 가입
		mvc.perform(
			post("/api/v1/challenge/participants/{roomId}/join", room.getId())
				.param("userId", user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// 중복으로 가입 시도
		ResultActions resultActions = mvc.perform(
			post("/api/v1/challenge/participants/{roomId}/join", room.getId())
				.param("userId", user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		ChallengeParticipantErrorCode error = ChallengeParticipantErrorCode.ALREADY_JOINED;

		resultActions
			.andExpect(handler().handlerType(ChallengeParticipantController.class))
			.andExpect(handler().methodName("joinChallengeRoom"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.resultCode").value(error.getCode()))
			.andExpect(jsonPath("$.msg").value(error.getMessage()));
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/participants/{roomId}/join` - Fail with NOT FOUND USER")
	void failWhenUserNotFound() throws Exception {
		Long wrongUserId = user.getId() + 1L;

		ResultActions resultActions = mvc.perform(
			post("/api/v1/challenge/participants/{roomId}/join", room.getId())
				.param("userId", wrongUserId.toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		ChallengeParticipantErrorCode error = ChallengeParticipantErrorCode.NOT_FOUND_USER;

		resultActions
			.andExpect(handler().handlerType(ChallengeParticipantController.class))
			.andExpect(handler().methodName("joinChallengeRoom"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.resultCode").value(error.getCode()))
			.andExpect(jsonPath("$.msg").value(error.getMessage()));
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/participants/{roomId}/join` - Fail with NOT FOUND ROOM")
	void failWhenRoomNotFound() throws Exception {
		Long wrongRoomId = room.getId() + 1L;

		ResultActions resultActions = mvc.perform(
			post("/api/v1/challenge/participants/{roomId}/join", wrongRoomId)
				.param("userId", user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		ChallengeParticipantErrorCode error = ChallengeParticipantErrorCode.CANNOT_FIND_CHALLENGE_ROOM;

		resultActions
			.andExpect(handler().handlerType(ChallengeParticipantController.class))
			.andExpect(handler().methodName("joinChallengeRoom"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.resultCode").value(error.getCode()))
			.andExpect(jsonPath("$.msg").value(error.getMessage()));
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/participants/{roomId}/join` - Fail when Room is Full")
	void failWhenRoomIsFull() throws Exception {
		// given - 꽉찬 방 만들기
		// 방 정원 2인 작은 방
		ChallengeRoom smallRoom = ChallengeRoomFactory.fakeChallengeRoom(user, 2);
		challengeRoomRepository.save(smallRoom);
		// 다른 유저 2명 가입시켜놓기
		User userA = userHelper.createUser();
		User userB = userHelper.createUser();
		challengeParticipantRepository.save(
			new ChallengeParticipant(userA, smallRoom, ChallengeParticipantRole.NORMAL));
		challengeParticipantRepository.save(
			new ChallengeParticipant(userB, smallRoom, ChallengeParticipantRole.NORMAL));

		// when - 정원초과로 가입 시도
		User userC = userHelper.createUser();

		ResultActions resultActions = mvc.perform(
			post("/api/v1/challenge/participants/{roomId}/join", smallRoom.getId())
				.param("userId", userC.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		ChallengeParticipantErrorCode error = ChallengeParticipantErrorCode.FULL_JOINED_ROOM;

		resultActions
			.andExpect(handler().handlerType(ChallengeParticipantController.class))
			.andExpect(handler().methodName("joinChallengeRoom"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.resultCode").value(error.getCode()))
			.andExpect(jsonPath("$.msg").value(error.getMessage()));
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/participants/{roomId}/leave` - Success Leave Challenge Room")
	void successLeaveChallengeRoom() throws Exception {
		// given - 가입
		mvc.perform(
			post("/api/v1/challenge/participants/{roomId}/join", room.getId())
				.param("userId", user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// when - 탈퇴
		ResultActions resultActions = mvc.perform(
			post("/api/v1/challenge/participants/{roomId}/leave", room.getId())
				.param("userId", user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		// then
		resultActions
			.andExpect(handler().handlerType(ChallengeParticipantController.class))
			.andExpect(handler().methodName("leaveChallengeRoom"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value(ChallengeParticipantHttp.LEAVE_SUCCESS_CODE))
			.andExpect(jsonPath("$.msg").value(ChallengeParticipantHttp.LEAVE_SUCCESS_MESSAGE));

		// 실제 DB 반영 확인
		ChallengeParticipant updated = challengeParticipantRepository
			.findByUserAndChallengeRoom(user, room)
			.orElseThrow();
		assertThat(updated.getQuited()).isTrue();
		assertThat(updated.getQuitDate()).isNotNull();
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/participants/{roomId}/leave` - Fail when user not in room")
	void failWhenNotParticipant() throws Exception {
		ResultActions resultActions = mvc.perform(
			post("/api/v1/challenge/participants/{roomId}/leave", room.getId())
				.param("userId", user.getId().toString())
				.contentType(MediaType.APPLICATION_JSON)
		).andDo(print());

		ChallengeParticipantErrorCode error = ChallengeParticipantErrorCode.NO_PARTICIPANT_IN_ROOM;

		resultActions
			.andExpect(handler().handlerType(ChallengeParticipantController.class))
			.andExpect(handler().methodName("leaveChallengeRoom"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.resultCode").value(error.getCode()))
			.andExpect(jsonPath("$.msg").value(error.getMessage()));
	}
}
