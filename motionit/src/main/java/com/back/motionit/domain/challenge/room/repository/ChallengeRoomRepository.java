package com.back.motionit.domain.challenge.room.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;

public interface ChallengeRoomRepository extends JpaRepository<ChallengeRoom, Long> {
}
