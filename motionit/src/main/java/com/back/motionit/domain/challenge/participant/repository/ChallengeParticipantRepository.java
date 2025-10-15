package com.back.motionit.domain.challenge.participant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.user.entity.User;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
	boolean existsByUserAndChallengeRoom(User user, ChallengeRoom challengeRoom);

	Integer countByChallengeRoom(ChallengeRoom challengeRoom);

	Optional<ChallengeParticipant> findByUserAndChallengeRoom(User user, ChallengeRoom challengeRoom);

	Integer countByChallengeRoomAndIsActiveTrue(ChallengeRoom challengeRoom);
}
