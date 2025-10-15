package com.back.motionit.helper;

import org.springframework.stereotype.Component;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.repository.ChallengeVideoRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.factory.ChallengeVideoFactory;

@Component
public class ChallengeVideoHelper {

	private ChallengeVideoRepository challengeVideoRepository;

	ChallengeVideoHelper(ChallengeVideoRepository challengeVideoRepository) {
		this.challengeVideoRepository = challengeVideoRepository;
	}

	public ChallengeVideo generateVideo(ChallengeRoom room, User user) {
		ChallengeVideo video = ChallengeVideoFactory.fakeChallengeVideo(user, room);
		return challengeVideoRepository.save(video);
	}
}
