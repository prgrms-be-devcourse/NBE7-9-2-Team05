package com.back.motionit.domain.challenge.video.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.external.youtube.YoutubeMetadataClient;
import com.back.motionit.domain.challenge.video.external.youtube.dto.YoutubeVideoMetadata;
import com.back.motionit.domain.challenge.video.repository.ChallengeVideoRepository;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.error.code.ChallengeVideoErrorCode;
import com.back.motionit.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeVideoService {

	private final ChallengeVideoRepository challengeVideoRepository;
	private final ChallengeRoomRepository challengeRoomRepository;
	private final UserRepository userRepository;
	private final YoutubeMetadataClient youtubeMetadataClient; // 유튜브 메타데이터 클라이언트

	public ChallengeVideo uploadChallengeVideo(Long userId, Long roomId, String youtubeUrl) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ChallengeVideoErrorCode.NOT_FOUND_USER));

		ChallengeRoom challengeRoom = challengeRoomRepository.findById(roomId)
			.orElseThrow(() -> new BusinessException(ChallengeVideoErrorCode.CANNOT_FIND_CHALLENGE_ROOM));

		YoutubeVideoMetadata metadata = youtubeMetadataClient.fetchMetedata(youtubeUrl);

		ChallengeVideo video = ChallengeVideo.of(challengeRoom, user, metadata, true);
		return challengeVideoRepository.save(video);
	}

	@Transactional(readOnly = true)
	public List<ChallengeVideo> getTodayMissionVideos(Long roomId) {
		return challengeVideoRepository.findByChallengeRoomIdAndMissionVideoTrueAndUploadDate(
			roomId, LocalDate.now()
		);
	}
}
