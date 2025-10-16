package com.back.motionit.domain.challenge.participant.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipantRole;
import com.back.motionit.domain.challenge.participant.repository.ChallengeParticipantRepository;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.exception.ServiceException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeParticipantService {

	private final ChallengeParticipantRepository challengeParticipantRepository;
	private final ChallengeRoomRepository challengeRoomRepository;
	private final UserRepository userRepository;

	public void joinChallengeRoom(Long userId, Long roomId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ServiceException("400", "유저를 찾을 수 없습니다"));

		ChallengeRoom challengeRoom = challengeRoomRepository.findByIdWithLock(roomId)
			.orElseThrow(() -> new ServiceException("400", "챌린지 룸을 찾을 수 없습니다"));

		boolean alreadyJoined = challengeParticipantRepository.existsByUserAndChallengeRoom(user, challengeRoom);
		if (alreadyJoined) {
			throw new ServiceException("400", "이미 해당 챌린지에 참가한 유저입니다");
		}

		Integer currentParticipants = challengeParticipantRepository.countByChallengeRoomAndQuitedFalse(challengeRoom);

		// 챌린지 룸의 현재 참가자 수가 최대 인원 수에 도달했는지 확인
		if (currentParticipants >= challengeRoom.getCapacity()) {
			throw new ServiceException("400", "챌린지 참가 인원이 초과되었습니다");
		}

		ChallengeParticipant participant = new ChallengeParticipant(user, challengeRoom,
			ChallengeParticipantRole.NORMAL);
		challengeParticipantRepository.save(participant);
	}

	public void leaveChallenge(Long userId, Long challengeRoomId) {

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new ServiceException("400", "챌린지 룸에 존재하지 않는 유저입니다"));

		ChallengeRoom challengeRoom = challengeRoomRepository.findById(challengeRoomId)
			.orElseThrow(() -> new ServiceException("400", "챌린지 룸을 찾을 수 없습니다"));

		ChallengeParticipant participant = challengeParticipantRepository
			.findByUserAndChallengeRoom(user, challengeRoom)
			.orElseThrow(() -> new ServiceException("400", "참가자가 아닙니다"));

		// Soft delete (isActive=false)
		participant.quitChallenge();
	}
}
