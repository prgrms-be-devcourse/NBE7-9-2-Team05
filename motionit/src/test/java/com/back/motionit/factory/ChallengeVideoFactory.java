package com.back.motionit.factory;

import java.time.LocalDate;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.user.entity.User;

public final class ChallengeVideoFactory extends BaseFactory {

	private ChallengeVideoFactory() {
		// 인스턴스화 방지
	}

	public static ChallengeVideo fakeChallengeVideo(User user, ChallengeRoom room) {
		return ChallengeVideo.builder()
			.challengeRoom(room)
			.user(user)
			.youtubeVideoId(generateYoutubeVideoId())
			.title(faker.lorem().sentence(3))
			.thumbnailUrl(faker.internet().url())
			.duration(faker.number().numberBetween(30, 600)) // 30초~10분
			.uploadDate(LocalDate.now())
			.isTodayMission(true)
			.build();
	}

	// 오늘 이전에 업로드된 영상 생성
	public static ChallengeVideo fakeOldVideo(User user, ChallengeRoom room) {
		return ChallengeVideo.builder()
			.challengeRoom(room)
			.user(user)
			.youtubeVideoId(generateYoutubeVideoId())
			.title(faker.lorem().sentence(3))
			.thumbnailUrl(faker.internet().url())
			.duration(faker.number().numberBetween(30, 600))
			.uploadDate(LocalDate.now().minusDays(1))
			.isTodayMission(false)
			.build();
	}

	private static String generateYoutubeVideoId() {
		return faker.regexify("[A-Za-z0-9_-]{11}");
	}
}
