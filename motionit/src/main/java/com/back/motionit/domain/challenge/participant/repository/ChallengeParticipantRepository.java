package com.back.motionit.domain.challenge.participant.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.user.entity.User;

public interface ChallengeParticipantRepository extends JpaRepository<ChallengeParticipant, Long> {
	boolean existsByUserAndChallengeRoom(User user, ChallengeRoom challengeRoom);

	Integer countByChallengeRoom(ChallengeRoom challengeRoom);

	Optional<ChallengeParticipant> findByUserAndChallengeRoom(User user, ChallengeRoom challengeRoom);

	Integer countByChallengeRoomAndQuitedFalse(ChallengeRoom challengeRoom);

	List<ChallengeParticipant> findAllByChallengeRoomAndQuitedFalse(ChallengeRoom room);

	Optional<ChallengeParticipant> findByUserIdAndChallengeRoomId(Long userId, Long roomId);

	@Query("""
			select p from ChallengeParticipant p
			join fetch p.user
			where p.challengeRoom.id = :roomId
		""")
	List<ChallengeParticipant> findAllByRoomIdWithUser(@Param("roomId") Long roomId);
}
