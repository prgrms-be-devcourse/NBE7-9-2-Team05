package com.back.motionit.domain.challenge.missionstatus.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

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

import com.back.motionit.domain.challenge.mission.api.response.ChallengeMissionStatusHttp;
import com.back.motionit.domain.challenge.mission.dto.ChallengeMissionCompleteRequest;
import com.back.motionit.domain.challenge.mission.entity.ChallengeMissionStatus;
import com.back.motionit.domain.challenge.mission.repository.ChallengeMissionStatusRepository;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.participant.repository.ChallengeParticipantRepository;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.repository.ChallengeVideoRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.factory.ChallengeMissionStatusFactory;
import com.back.motionit.factory.ChallengeParticipantFactory;
import com.back.motionit.factory.ChallengeRoomFactory;
import com.back.motionit.factory.ChallengeVideoFactory;
import com.back.motionit.global.error.code.ChallengeMissionErrorCode;
import com.back.motionit.helper.UserHelper;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@Transactional
public class ChallengeMissionsStatusControllerTest {

	@Autowired
	MockMvc mvc;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	ChallengeMissionStatusRepository challengeMissionStatusRepository;
	@Autowired
	ChallengeParticipantRepository challengeParticipantRepository;
	@Autowired
	ChallengeRoomRepository challengeRoomRepository;
	@Autowired
	ChallengeVideoRepository challengeVideoRepository;
	@Autowired
	UserHelper userHelper;

	private ChallengeRoom room;
	private ChallengeParticipant participant;
	private ChallengeVideo video;
	private LocalDate today;

	// 5명 정원 방, 1명 참가, 1개 영상, 오늘 날짜 미션 상태 생성
	@BeforeEach
	void setUp() {
		User user = userHelper.createUser();
		room = challengeRoomRepository.save(ChallengeRoomFactory.fakeChallengeRoom(user, 5));
		participant = challengeParticipantRepository.save(
			ChallengeParticipantFactory.fakeParticipant(user, room));
		video = challengeVideoRepository.save(ChallengeVideoFactory.fakeChallengeVideo(user, room));
		today = LocalDate.now();

		challengeMissionStatusRepository.save(ChallengeMissionStatusFactory.fakeMission(participant));
	}

	@Test
	@DisplayName("POST /rooms/{roomId}/missions/complete - 미션 완료 처리")
	void completeMissionSuccess() throws Exception {
		ChallengeMissionCompleteRequest request = new ChallengeMissionCompleteRequest(participant.getId(),
			video.getId());

		mvc.perform(post("/api/v1/challenge/rooms/{roomId}/missions/complete", room.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value(ChallengeMissionStatusHttp.MISSION_COMPLETE_SUCCESS_MESSAGE))
			.andExpect(jsonPath("$.data.completed").value(true))
			.andDo(print());
	}

	@Test
	@DisplayName("미션 완료처리 실패 - 잘못된 방 접근")
	void completeMissionFail_InvalidRoomAccess() throws Exception {
		// 다른 방 생성
		ChallengeRoom otherRoom = challengeRoomRepository.save(ChallengeRoomFactory.fakeChallengeRoom(
			userHelper.createUser(), 5));
		ChallengeMissionCompleteRequest request = new ChallengeMissionCompleteRequest(participant.getId(),
			video.getId());

		// 다른(참여중이지 않은) 방에 미션완료처리 요청
		mvc.perform(post("/api/v1/challenge/rooms/{roomId}/missions/complete", otherRoom.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.msg").value(ChallengeMissionErrorCode.INVALID_ROOM_ACCESS.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("미션 완료처리 실패 - 존재하지 않는 영상")
	void completeMissionFail_videoNotFound() throws Exception {
		ChallengeMissionCompleteRequest request = new ChallengeMissionCompleteRequest(participant.getId(), 9999L);

		// 존재하지 않는 영상 ID로 미션완료처리 요청
		mvc.perform(post("/api/v1/challenge/rooms/{roomId}/missions/complete", room.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.msg").value(ChallengeMissionErrorCode.NOT_FOUND_VIDEO.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("미션 완료 실패 - 이미 완료된 미션")
	void completeMission_alreadyCompleted() throws Exception {
		ChallengeMissionStatus mission = challengeMissionStatusRepository.findByParticipantIdAndMissionDate(
			participant.getId(), today).get();
		mission.completeMission(video);
		challengeMissionStatusRepository.save(mission);

		ChallengeMissionCompleteRequest request =
			new ChallengeMissionCompleteRequest(participant.getId(), video.getId());

		mvc.perform(post("/api/v1/challenge/rooms/{roomId}/missions/complete", room.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.msg").value(ChallengeMissionErrorCode.ALREADY_COMPLETED.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("미션 완료 실패 - 초기화되지 않은 미션")
	void completeMission_notInitialized() throws Exception {
		// 다른 participant는 mission 초기화 안 됨
		ChallengeParticipant newParticipant = challengeParticipantRepository.save(
			ChallengeParticipantFactory.fakeParticipant(userHelper.createUser(), room)
		);
		ChallengeMissionCompleteRequest request =
			new ChallengeMissionCompleteRequest(newParticipant.getId(), video.getId());

		mvc.perform(post("/api/v1/challenge/rooms/{roomId}/missions/complete", room.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.msg").value(ChallengeMissionErrorCode.NOT_INITIALIZED_MISSION.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("미션 완료 실패 - 참가자 없음")
	void completeMission_notFoundParticipant() throws Exception {
		ChallengeMissionCompleteRequest request =
			new ChallengeMissionCompleteRequest(9999L, video.getId());

		mvc.perform(post("/api/v1/challenge/rooms/{roomId}/missions/complete", room.getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().is4xxClientError())
			.andExpect(jsonPath("$.msg").value(ChallengeMissionErrorCode.NOT_FOUND_USER.getMessage()))
			.andDo(print());
	}

	@Test
	@DisplayName("GET /rooms/{roomId}/missions/{participantId}/today - 성공")
	void getTodayMissionStatus_success() throws Exception {
		mvc.perform(get("/api/v1/challenge/rooms/{roomId}/missions/{participantId}/today",
				room.getId(), participant.getId())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value(ChallengeMissionStatusHttp.GET_TODAY_PARTICIPANT_SUCCESS_MESSAGE))
			.andExpect(jsonPath("$.data.participantId").value(participant.getId()))
			.andDo(print());
	}

	@Test
	@DisplayName("GET /rooms/{roomId}/missions/today - 방 전체 오늘의 미션 조회 성공")
	void getTodayMissionByRoom_success() throws Exception {
		mvc.perform(get("/api/v1/challenge/rooms/{roomId}/missions/today", room.getId())
				.accept(MediaType.APPLICATION_JSON))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.msg").value(ChallengeMissionStatusHttp.GET_TODAY_SUCCESS_MESSAGE))
			.andExpect(jsonPath("$.data").isArray())
			.andDo(print());
	}
}
