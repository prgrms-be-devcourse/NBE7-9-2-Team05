package com.back.motionit.domain.challenge.video.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;

public interface ChallengeVideoRepository extends JpaRepository<ChallengeVideo, Long> {
}
