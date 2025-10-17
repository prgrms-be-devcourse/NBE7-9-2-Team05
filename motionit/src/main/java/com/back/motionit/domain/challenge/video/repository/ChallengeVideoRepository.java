package com.back.motionit.domain.challenge.video.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;

public interface ChallengeVideoRepository extends JpaRepository<ChallengeVideo, Long> {
	List<ChallengeVideo> findByChallengeRoomIdAndMissionVideoTrueAndUploadDate(Long roomId, LocalDate uploadDate);
}
