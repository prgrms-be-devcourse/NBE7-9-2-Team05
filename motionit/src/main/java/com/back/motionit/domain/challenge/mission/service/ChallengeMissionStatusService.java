package com.back.motionit.domain.challenge.mission.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	public ChallengeMissionStatus completeMission(Long roomId, Long actorId) {
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

		// TODO: 쿼리 최적화 필요 (LEFT JOIN)
		List<ChallengeParticipant> participants = challengeParticipantRepository.findAllByChallengeRoom(challengeRoom);

		// 오늘 미션 완료자 조회 (참가자까지 fetch)
		List<ChallengeMissionStatus> missions =
			challengeMissionStatusRepository.findByRoomAndDate(challengeRoom, today);

		// participantId → mission 매핑
		Map<Long, ChallengeMissionStatus> missionMap = missions.stream()
			.filter(m -> m.getParticipant() != null) // null-safe
			.collect(Collectors.toMap(
				m -> m.getParticipant().getId(),
				m -> m,
				(a, b) -> a // 중복 키 방지
			));

		// 전체 참가자 기준 병합 (미완료자 포함)
		List<ChallengeMissionStatus> allStatuses = participants.stream()
			.map(p -> missionMap.getOrDefault(
				p.getId(),
				new ChallengeMissionStatus(p, today) // 엔티티 생성자 이용 (participant 연결 명시)
			))
			.toList();

		if (allStatuses.isEmpty()) {
			log.warn("[getTodayMissionsByRoom] {} 날짜에 미션 데이터가 없습니다. (roomId={})", today, roomId);
		}
		return allStatuses;
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
