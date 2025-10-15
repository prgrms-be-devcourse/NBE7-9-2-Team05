package com.back.motionit.factory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.entity.OpenStatus;
import com.back.motionit.domain.user.entity.User;

public final class ChallengeRoomFactory extends BaseFactory {
	public static ChallengeRoom fakeChallengeRoom(User user) {
		LocalDateTime now = LocalDateTime.now();
		int startOffsetDays = faker.number().numberBetween(0, 7); // 오늘~7일 내 시작
		int durationDays = faker.number().numberBetween(7, 30); // 1~4주 진행

		LocalDateTime start = now.plusDays(startOffsetDays);
		LocalDateTime end = start.plusDays(durationDays);

		List<ChallengeVideo> videos = new ArrayList<>();

		return new ChallengeRoom(
			user,
			faker.lorem().sentence(3, 5),    // title
			faker.lorem().paragraph(),    // description
			faker.number().numberBetween(2, 100),    // capacity
			faker.options().option(OpenStatus.class),
			start,
			end,
			faker.internet().url(),
			videos
		);
	}
}
