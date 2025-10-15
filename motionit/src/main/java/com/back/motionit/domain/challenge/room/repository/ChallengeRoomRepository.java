package com.back.motionit.domain.challenge.room.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;

import jakarta.persistence.LockModeType;

public interface ChallengeRoomRepository extends JpaRepository<ChallengeRoom, Long> {
	// PESSIMISTIC_WRITE 락을 사용하여 해당 챌린지룸을 수정할 때 다른 트랜잭션이 접근하지 못하도록 함
	// 유저 참가시에 정원 초과를 방지하기 위해 사용, 단순 조회(목록 등)에는 절대 쓰지 말것
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM ChallengeRoom c WHERE c.id = :id")
	Optional<ChallengeRoom> findByIdWithLock(@Param("id") Long id);
}
