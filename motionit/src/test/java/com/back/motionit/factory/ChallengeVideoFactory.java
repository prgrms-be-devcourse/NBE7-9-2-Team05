package com.back.motionit.factory;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.user.entity.User;

public final class ChallengeVideoFactory extends BaseFactory {

	public static ChallengeVideo fakeChallengeVideo(User user, ChallengeRoom room) {
		return ChallengeVideo.builder()
			.user(user)
			.challengeRoom(room)
			.youtubeVideoId(generateYoutubeVideoId())
			.title(faker.lorem().sentence(3))
			.thumbnailUrl(faker.internet().url())
			.duration(faker.number().numberBetween(30, 600))
			.isTodayMission(faker.bool().bool())
			.build();
	}

	public static String generateYoutubeVideoId() {
		return faker.regexify("[A-Za-z0-9_-]{11}");
	}
}
