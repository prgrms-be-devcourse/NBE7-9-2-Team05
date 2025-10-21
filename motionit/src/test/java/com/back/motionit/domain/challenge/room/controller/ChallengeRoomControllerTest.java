package com.back.motionit.domain.challenge.room.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipantRole;
import com.back.motionit.domain.challenge.participant.repository.ChallengeParticipantRepository;
import com.back.motionit.domain.challenge.room.api.response.ChallengeRoomHttp;
import com.back.motionit.domain.challenge.room.builder.CreateRoomRequestBuilder;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.error.code.ChallengeRoomErrorCode;
import com.back.motionit.global.error.code.CommonErrorCode;
import com.back.motionit.global.error.exception.BusinessException;
import com.back.motionit.helper.UserHelper;
import com.back.motionit.security.SecurityUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@Transactional
public class ChallengeRoomControllerTest {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ChallengeRoomRepository challengeRoomRepository;

	@Autowired
	private ChallengeParticipantRepository challengeParticipantRepository;

	@Autowired
	private UserHelper userHelper;

	private CreateRoomRequestBuilder createRoomRequestBuilder;
	private User user;
	private ObjectMapper mapper = new ObjectMapper();

	SecurityUser securityUser;
	UsernamePasswordAuthenticationToken authentication;

	@BeforeEach
	public void setUp() {
		createRoomRequestBuilder = new CreateRoomRequestBuilder();
		user = userHelper.createUser();
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/rooms` - Success Create Challenge Room")
	void successCreateRoom() throws Exception {
		Map<String, String> params = createRoomRequestBuilder.toParamMap();

		var authorities = AuthorityUtils.createAuthorityList("ROLE");
		securityUser = new SecurityUser(user.getId(), user.getPassword(), user.getNickname(), authorities);
		authentication =
			new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String requestJson = mapper.writeValueAsString(Map.of(
			"title", params.get("title"),
			"description", params.get("description"),
			"capacity", Integer.valueOf(params.get("capacity")),
			"duration", Integer.valueOf(params.get("duration")),
			"videoUrl", params.get("videoUrl"),
			"imageFileName", params.get("imageFileName"),
			"contentType", params.get("contentType")
		));

		ResultActions resultActions = mvc.perform(
			post("/api/v1/challenge/rooms")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
		).andDo(print());

		resultActions
			.andExpect(handler().handlerType(ChallengeRoomController.class))
			.andExpect(handler().methodName("createRoom"))
			.andExpect(jsonPath("$.resultCode").value(ChallengeRoomHttp.CREATE_ROOM_SUCCESS_CODE))
			.andExpect(jsonPath("$.msg").value(ChallengeRoomHttp.CREATE_ROOM_SUCCESS_MESSAGE));

		MvcResult mvcResult = resultActions.andReturn();

		String responseJson = mvcResult.getResponse().getContentAsString();
		String title = JsonPath.read(responseJson, "$.data.title");
		String image = JsonPath.read(responseJson, "$.data.roomImage");
		long id = JsonPath.<Number>read(responseJson, "$.data.id").longValue();

		ChallengeRoom createdRoom = challengeRoomRepository.findById(id).orElseThrow(() ->
			new BusinessException(CommonErrorCode.NOT_FOUND)
		);

		assertThat(title).isEqualTo(params.get("title"));
		assertThat(createdRoom.getRoomImage()).isEqualTo(image);
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/rooms` - Failed with NOT FOUND USER")
	void notFoundUserId() throws Exception {
		Map<String, String> params = createRoomRequestBuilder.toParamMap();

		Long wrongUserId = user.getId() + 1L;
		var authorities = AuthorityUtils.createAuthorityList("ROLE");
		securityUser = new SecurityUser(wrongUserId, user.getPassword(), user.getNickname(), authorities);
		authentication =
			new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String requestJson = mapper.writeValueAsString(Map.of(
			"title", params.get("title"),
			"description", params.get("description"),
			"capacity", Integer.valueOf(params.get("capacity")),
			"duration", Integer.valueOf(params.get("duration")),
			"videoUrl", params.get("videoUrl"),
			"imageFileName", params.get("imageFileName"),
			"contentType", params.get("contentType")
		));

		ResultActions resultActions = mvc.perform(
			post("/api/v1/challenge/rooms")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
		).andDo(print());

		ChallengeRoomErrorCode error = ChallengeRoomErrorCode.NOT_FOUND_USER;

		resultActions
			.andExpect(handler().handlerType(ChallengeRoomController.class))
			.andExpect(handler().methodName("createRoom"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.resultCode").value(error.getCode()))
			.andExpect(jsonPath("$.msg").value(error.getMessage()));
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/rooms` - Auto Join as Host after Create Room")
	void autoJoinAsHostAfterCreateRoom() throws Exception {
		// given
		User owner = userHelper.createUser();
		Map<String, String> params = createRoomRequestBuilder.toParamMap();

		var authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
		securityUser = new SecurityUser(owner.getId(), owner.getPassword(), owner.getNickname(), authorities);
		authentication =
			new UsernamePasswordAuthenticationToken(securityUser, null, securityUser.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(authentication);

		String requestJson = mapper.writeValueAsString(Map.of(
			"title", params.get("title"),
			"description", params.get("description"),
			"capacity", Integer.valueOf(params.get("capacity")),
			"duration", Integer.valueOf(params.get("duration")),
			"videoUrl", params.get("videoUrl"),
			"imageFileName", params.get("imageFileName"),
			"contentType", params.get("contentType")
		));

		// when
		MvcResult result = mvc.perform(post("/api/v1/challenge/rooms")
				.contentType(MediaType.APPLICATION_JSON)
				.content(requestJson)
			)
			.andExpect(jsonPath("$.resultCode").value(ChallengeRoomHttp.CREATE_ROOM_SUCCESS_CODE))
			.andExpect(jsonPath("$.msg").value(ChallengeRoomHttp.CREATE_ROOM_SUCCESS_MESSAGE))
			.andReturn();

		// then
		String responseJson = result.getResponse().getContentAsString();
		Long roomId = JsonPath.<Number>read(responseJson, "$.data.id").longValue();
		ChallengeRoom createdRoom = challengeRoomRepository.findById(roomId)
			.orElseThrow();
		ChallengeParticipant participant = challengeParticipantRepository
			.findByUserAndChallengeRoom(owner, createdRoom)
			.orElseThrow();

		assertThat(participant.getRole()).isEqualTo(ChallengeParticipantRole.HOST);
	}

	@AfterEach
	void clear() {
		SecurityContextHolder.clearContext();
	}
}
