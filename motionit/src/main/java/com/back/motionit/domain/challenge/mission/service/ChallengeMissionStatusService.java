package com.back.motionit.domain.challenge.mission.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.mission.entity.ChallengeMissionStatus;
import com.back.motionit.domain.challenge.mission.repository.ChallengeMissionStatusRepository;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.participant.repository.ChallengeParticipantRepository;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.video.repository.ChallengeVideoRepository;
import com.back.motionit.global.error.code.ChallengeMissionErrorCode;
import com.back.motionit.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChallengeMissionStatusService {

	private final ChallengeMissionStatusRepository challengeMissionStatusRepository;
	private final ChallengeRoomRepository challengeRoomRepository;
	private final ChallengeParticipantRepository challengeParticipantRepository;
	private final ChallengeVideoRepository challengeVideoRepository;

	@Transactional
	public ChallengeMissionStatus completeMission(Long roomId, Long actorId, Long videoId) {
		ChallengeParticipant participant = getParticipantOrThrow(actorId, roomId);

		// 영상 존재 확인
		boolean hasTodayVideo = challengeVideoRepository
			.existsByChallengeRoomIdAndUploadDate(roomId, LocalDate.now());

		if (!hasTodayVideo) {
			throw new BusinessException(ChallengeMissionErrorCode.NO_VIDEO_UPLOADED);
		}
		LocalDate today = LocalDate.now();

		ChallengeMissionStatus mission = challengeMissionStatusRepository
			.findByParticipantIdAndMissionDate(participant.getId(), today)
			.orElseGet(() -> {
				ChallengeMissionStatus newMission = new ChallengeMissionStatus(participant, today);
				return challengeMissionStatusRepository.save(newMission);
			});

		// 이미 완료된 미션인지 확인
		if (Boolean.TRUE.equals(mission.getCompleted())) {
			throw new BusinessException(ChallengeMissionErrorCode.ALREADY_COMPLETED);
		}

		// 미션 완료 상태로 업데이트
		mission.completeMission();
		return mission;
	}

	@Transactional(readOnly = true)
	public ChallengeMissionStatus getTodayMissionStatus(Long roomId, Long actorId) {
		ChallengeParticipant participant = getParticipantOrThrow(actorId, roomId);

		LocalDate today = LocalDate.now();

		return challengeMissionStatusRepository
			.findByParticipantIdAndMissionDate(participant.getId(), today)
			.orElseThrow(() -> new BusinessException(ChallengeMissionErrorCode.NOT_INITIALIZED_MISSION));
	}

	// 특정 운동방의 모든 참가자의 오늘 미션 상태 조회
	@Transactional(readOnly = true)
	public List<ChallengeMissionStatus> getTodayMissionsByRoom(Long roomId, Long actorId) {
		// 접근 권한 확인
		ChallengeParticipant participant = getParticipantOrThrow(actorId, roomId);
		ChallengeRoom challengeRoom = participant.getChallengeRoom();

		LocalDate today = LocalDate.now();
		// 모든 참가자의 미션 기록을 조회
		// 스케줄러 미작동 / 신규 운동방-참가자 등의 이슈로 인해 미션 기록이 없을 수 있음 -> 빈 리스트 반환
		List<ChallengeMissionStatus> missions = challengeMissionStatusRepository.findByRoomAndDate(challengeRoom,
			today);

		if (missions.isEmpty()) {
			log.warn("[getTodayMissionsByRoom] {} 날짜에 미션 데이터가 없습니다. (roomId={})", today, roomId);
		}
		return missions;
	}

	// 참가자의 미션 수행 내역 조회
	@Transactional(readOnly = true)
	public List<ChallengeMissionStatus> getMissionHistory(Long roomId, Long actorId) {
		ChallengeParticipant participant = getParticipantOrThrow(actorId, roomId);

		return challengeMissionStatusRepository.findAllByParticipantId(participant.getId());
	}

	private ChallengeParticipant getParticipantOrThrow(Long userId, Long roomId) {
		return challengeParticipantRepository.findByUserIdAndChallengeRoomId(userId, roomId)
			.orElseThrow(() -> new BusinessException(ChallengeMissionErrorCode.INVALID_ROOM_ACCESS));
	}
}
