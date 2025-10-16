package com.back.motionit.domain.challenge.room.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.room.builder.CreateRoomRequestBuilder;
import com.back.motionit.domain.challenge.room.constant.ChallengeRoomHttp;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.helper.ChallengeRoomHelper;
import com.back.motionit.helper.UserHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

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
	private ChallengeRoomHelper challengeRoomHelper;

	@Autowired
	private UserHelper userHelper;

	private CreateRoomRequestBuilder createRoomRequestBuilder;
	private Long roomId;
	private User user;
	private MockMultipartFile imageFile;
	private ObjectMapper mapper = new ObjectMapper();

	@BeforeEach
	public void setUp() {
		createRoomRequestBuilder = new CreateRoomRequestBuilder();
		user = userHelper.createUser();
		imageFile = new MockMultipartFile(
			"image",
			"test-image.jpg",
			MediaType.IMAGE_JPEG_VALUE,
			"fake image bytes".getBytes()
		);
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/rooms` - Create Challenge Room")
	void createRoomTest() throws Exception {
		Map<String, String> params = createRoomRequestBuilder.toParamMap();

		//TODO: it should be removed after complete to develop auth feature
		params.put("userId", user.getId().toString());

		String requestJson = mapper.writeValueAsString(Map.of(
			"userId", user.getId(),
			"title", params.get("title"),
			"description", params.get("description"),
			"capacity", Integer.valueOf(params.get("capacity")),
			"duration", Integer.valueOf(params.get("duration")),
			"videoUrl", params.get("videoUrl")
		));

		MockMultipartFile requestPart = new MockMultipartFile(
			"request", "request.json",
			MediaType.APPLICATION_JSON_VALUE,
			requestJson.getBytes()
		);

		ResultActions resultActions = mvc.perform(
			multipart("/api/v1/challenge/rooms")
				.file(requestPart)
				.file(imageFile)
				.contentType(MediaType.MULTIPART_FORM_DATA)
		).andDo(print());

		resultActions
			.andExpect(handler().handlerType(ChallengeRoomController.class))
			.andExpect(handler().methodName("createRoom"))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.resultCode").value(ChallengeRoomHttp.CREATE_ROOM_SUCCESS_CODE))
			.andExpect(jsonPath("$.msg").value(ChallengeRoomHttp.CREATE_ROOM_SUCCESS_MESSAGE));

		MvcResult mvcResult = resultActions.andReturn();

		String responseJson = mvcResult.getResponse().getContentAsString();
		String title = JsonPath.read(responseJson, "$.data.title");
		Long id = JsonPath.<Number>read(responseJson, "$.data.id").longValue();

		ChallengeRoom room = challengeRoomRepository.findById(id)
			.orElseThrow(() -> new IllegalArgumentException("404, Not found room for id: %s".formatted(id)));

		assertThat(title).isEqualTo(params.get("title"));
		assertThat(room.getUser().getId()).isEqualTo(user.getId());
	}
}
