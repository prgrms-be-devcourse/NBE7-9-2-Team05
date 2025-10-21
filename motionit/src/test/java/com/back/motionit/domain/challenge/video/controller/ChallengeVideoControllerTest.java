package com.back.motionit.domain.challenge.video.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.video.api.response.ChallengeVideoHttp;
import com.back.motionit.domain.challenge.video.dto.ChallengeVideoUploadRequest;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.repository.ChallengeVideoRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.factory.ChallengeRoomFactory;
import com.back.motionit.factory.ChallengeVideoFactory;
import com.back.motionit.helper.UserHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ChallengeVideoControllerTest {

	@Autowired
	MockMvc mvc;
	@Autowired
	ChallengeVideoRepository challengeVideoRepository;
	@Autowired
	ChallengeRoomRepository challengeRoomRepository;
	@Autowired
	UserHelper userHelper;
	@Autowired
	ObjectMapper objectMapper;

	@Autowired
	private RequestMappingHandlerAdapter adapter;

	// JSON 변환기 확인용
	@Test
	public void printConverters() {
		adapter.getMessageConverters().forEach(c ->
			System.out.println("✅ " + c.getClass().getName())
		);
	}

	private User user;
	private ChallengeRoom room;

	@BeforeEach
	void setUp() {
		challengeVideoRepository.deleteAll();
		user = userHelper.createUser();
		room = challengeRoomRepository.save(ChallengeRoomFactory.fakeChallengeRoom(user, 5));
	}

	@Test
	@DisplayName("POST `/api/v1/challenge/rooms/{roomId}/videos` - success")
	void successUploadVideo() throws Exception {
		ChallengeVideoUploadRequest request = new ChallengeVideoUploadRequest(
			"https://www.youtube.com/watch?v=dQw4w9WgXcQ"
		);
		String requestJson = objectMapper.writeValueAsString(request);

		mvc.perform(post("/api/v1/challenge/rooms/{roomId}/videos", room.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.characterEncoding("UTF-8")
				.content(requestJson))
			.andExpect(handler().handlerType(ChallengeVideoController.class))
			.andExpect(handler().methodName("uploadVideo"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value(ChallengeVideoHttp.UPLOAD_SUCCESS_MESSAGE))
			.andDo(print());
	}

	@Test
	@DisplayName("GET `/api/v1/challenge/rooms/{roomId}/videos/today` - 오늘의 미션 영상 조회 성공")
	void successGetTodayMissionVideos() throws Exception {
		// given
		List<ChallengeVideo> videos = List.of(
			challengeVideoRepository.save(ChallengeVideoFactory.fakeChallengeVideo(user, room)),
			challengeVideoRepository.save(ChallengeVideoFactory.fakeChallengeVideo(user, room))
		);

		// when & then
		mvc.perform(get("/api/v1/challenge/rooms/{roomId}/videos/today", room.getId())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(handler().handlerType(ChallengeVideoController.class))
			.andExpect(handler().methodName("getTodayMissionVideos"))
			.andExpect(jsonPath("$.msg").value(ChallengeVideoHttp.GET_TODAY_MISSION_SUCCESS_MESSAGE))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(print());
	}

	@Test
	@DisplayName("DELETE `/api/v1/challenge/videos/{videoId}?userId={userId}` - 영상 삭제 성공")
	void successDeleteVideo() throws Exception {
		// given
		ChallengeVideo video = challengeVideoRepository.save(
			ChallengeVideoFactory.fakeChallengeVideo(user, room)
		);

		// when & then
		mvc.perform(delete("/api/v1/challenge/videos/{videoId}", video.getId())
				.param("userId", String.valueOf(user.getId())))
			.andExpect(status().isOk())
			.andExpect(handler().handlerType(ChallengeVideoController.class))
			.andExpect(handler().methodName("deleteVideoByUser"))
			.andExpect(jsonPath("$.msg").value(ChallengeVideoHttp.DELETE_SUCCESS_MESSAGE))
			.andDo(print());
	}
}
