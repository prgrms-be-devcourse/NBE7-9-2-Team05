package com.back.motionit.factory;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.user.entity.User;

public final class ChallengeVideoFactory extends BaseFactory {

	public static ChallengeVideo fakeChallengeVideo(User user, ChallengeRoom room) {
		return new ChallengeVideo(
			generateYoutubeVideoId(),
			room,
			user
		);
	}

	public static String generateYoutubeVideoId() {
		return faker.regexify("[A-Za-z0-9_-]{11}");
	}
}