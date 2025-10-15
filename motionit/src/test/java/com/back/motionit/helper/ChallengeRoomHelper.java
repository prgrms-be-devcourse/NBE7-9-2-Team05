package com.back.motionit.helper;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.video.repository.ChallengeVideoRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.factory.ChallengeRoomFactory;

@Component
public class ChallengeRoomHelper {

	private ChallengeRoomRepository challengeRoomRepository;
	private ChallengeVideoRepository challengeVideoRepository;

	ChallengeRoomHelper(
		ChallengeRoomRepository challengeRoomRepository,
		ChallengeVideoRepository challengeVideoRepository
	) {
		this.challengeRoomRepository = challengeRoomRepository;
		this.challengeVideoRepository = challengeVideoRepository;
	}

	public ChallengeRoom createChallengeRoom(Optional<Long> roomId, User user) {
		ChallengeRoom room = ChallengeRoomFactory.fakeChallengeRoom(user);

		if (roomId.isPresent()) {
			ChallengeRoomFactory.withId(room, roomId.get());
		}

		return challengeRoomRepository.save(room);
	}

	public void clearChallengeRoom() {
		challengeVideoRepository.deleteAll();
		challengeRoomRepository.deleteAll();
	}
}
