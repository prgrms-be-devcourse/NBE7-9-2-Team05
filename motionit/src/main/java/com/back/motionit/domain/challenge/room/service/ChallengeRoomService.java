package com.back.motionit.domain.challenge.room.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.back.motionit.domain.challenge.room.dto.CreateRoomRequest;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.entity.OpenStatus;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.exception.ServiceException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeRoomService {

	private final ChallengeRoomRepository challengeRoomRepository;
	private final UserRepository userRepository;

	public ChallengeRoom createRoom(CreateRoomRequest input, MultipartFile image) {
		ChallengeRoom room = mapToRoomObject(input, image);
		return challengeRoomRepository.save(room);
	}

	public ChallengeRoom mapToRoomObject(CreateRoomRequest input, MultipartFile image) {
		Long userId = input.userId();

		//TODO: will be refactor after define team error code rule
		User user = userRepository.findById(userId).orElseThrow(() -> new ServiceException("404", "해당 유저를 찾을 수 없습니다."));

		LocalDateTime now = LocalDateTime.now();
		int durationDays = input.duration();

		LocalDateTime start = now;
		LocalDateTime end = start.plusDays(durationDays);

		List<ChallengeVideo> videos = new ArrayList<>();

		return new ChallengeRoom(
			user,
			input.title(),
			input.description(),
			input.capacity(),
			OpenStatus.OPEN,
			start,
			end,
			image.toString(), // TODO: will be refactor to AWS S3 url
			videos
		);
	}
}
