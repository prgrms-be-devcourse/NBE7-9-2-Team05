package com.back.motionit.domain.challenge.video.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.video.api.ChallengeVideoApi;
import com.back.motionit.domain.challenge.video.api.response.ChallengeVideoHttp;
import com.back.motionit.domain.challenge.video.dto.ChallengeVideoResponse;
import com.back.motionit.domain.challenge.video.dto.ChallengeVideoUploadRequest;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.service.ChallengeVideoService;
import com.back.motionit.global.request.RequestContext;
import com.back.motionit.global.respoonsedata.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/challenge")
@RequiredArgsConstructor
public class ChallengeVideoController implements ChallengeVideoApi {

	private final ChallengeVideoService challengeVideoService;
	private final RequestContext requestContext;

	@PostMapping("/rooms/{roomId}/videos")
	public ResponseData<ChallengeVideoResponse> uploadVideo(
		@PathVariable Long roomId,
		@RequestBody @Valid ChallengeVideoUploadRequest request
	) {
		//User actor = requestContext.getActor();
		Long mockUserId = 7L;

		ChallengeVideo savedVideo = challengeVideoService.uploadChallengeVideo(
			mockUserId, roomId, request.youtubeUrl()
		);
		return ResponseData.success(ChallengeVideoHttp.UPLOAD_SUCCESS_MESSAGE, ChallengeVideoResponse.from(savedVideo));
	}

	@Override
	public ResponseData<List<ChallengeVideoResponse>> getTodayMissionVideos(@PathVariable Long roomId) {
		// User actor = requestContext.getActor();
		Long mockUserId = 7L;

		List<ChallengeVideoResponse> videos = challengeVideoService.getTodayMissionVideos(mockUserId, roomId)
			.stream()
			.map(ChallengeVideoResponse::from)
			.toList();

		return ResponseData.success(ChallengeVideoHttp.GET_TODAY_MISSION_SUCCESS_MESSAGE, videos);
	}

	@Override
	public ResponseData<Void> deleteVideoByUser(
		@PathVariable Long videoId
	) {
		// User actor = requestContext.getActor();
		Long mockUserId = 7L;

		challengeVideoService.deleteVideoByUser(mockUserId, videoId);
		return ResponseData.success(ChallengeVideoHttp.DELETE_SUCCESS_MESSAGE, null);
	}
}
