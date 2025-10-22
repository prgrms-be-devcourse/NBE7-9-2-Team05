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

	Optional<ChallengeParticipant> findByUserAndChallengeRoom(User user, ChallengeRoom challengeRoom);

	Integer countByChallengeRoomAndQuitedFalse(ChallengeRoom challengeRoom);

	List<ChallengeParticipant> findAllByChallengeRoomAndQuitedFalse(ChallengeRoom room);

	@Query("""
			select p from ChallengeParticipant p
			join fetch p.user
			where p.challengeRoom.id = :roomId
		""")
	List<ChallengeParticipant> findAllByRoomIdWithUser(@Param("roomId") Long roomId);

	@Query("""
			select p from ChallengeParticipant p
			where p.user.id = :userId
			and p.challengeRoom.id = :roomId
			and p.quited = false
		""")
	Optional<ChallengeParticipant> findActiveParticipant(@Param("userId") Long userId,
		@Param("roomId") Long roomId);

	@Query("""
			select cp
			from ChallengeParticipant cp
			join fetch cp.challengeRoom cr
			where cp.user.id = :userId
			and cr.id = :roomId
			and cp.quited = false
		""")
	Optional<ChallengeParticipant> findActiveWithRoom(@Param("userId") Long userId,
		@Param("roomId") Long roomId);
}
