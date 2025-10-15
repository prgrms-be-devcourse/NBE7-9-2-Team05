package com.back.motionit.domain.challenge.room.service;

import org.springframework.stereotype.Service;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeRoomService {

	private final ChallengeRoomRepository challengeRoomRepository;

	public ChallengeRoom createRoom() {
		return new ChallengeRoom();
	}
}
