package com.back.motionit.domain.challenge.video.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.video.api.ChallengeVideoApi;
import com.back.motionit.domain.challenge.video.dto.ChallengeVideoResponse;
import com.back.motionit.domain.challenge.video.dto.ChallengeVideoUploadRequest;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.service.ChallengeVideoService;
import com.back.motionit.global.respoonsedata.ResponseData;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/challenge")
@RequiredArgsConstructor
public class ChallengeVideoController implements ChallengeVideoApi {

	private final ChallengeVideoService challengeVideoService;

	@Override
	public ResponseData<ChallengeVideoResponse> uploadVideo(
		@PathVariable Long roomId,
		@RequestBody @Valid ChallengeVideoUploadRequest request
	) {
		ChallengeVideo savedVideo = challengeVideoService.uploadChallengeVideo(
			request.userId(), roomId, request.youtubeUrl()
		);
		return ResponseData.success("영상 업로드 성공", ChallengeVideoResponse.from(savedVideo));
	}

	@Override
	public ResponseData<List<ChallengeVideoResponse>> getTodayMissionVideos(@PathVariable Long roomId) {
		List<ChallengeVideoResponse> videos = challengeVideoService.getTodayMissionVideos(roomId)
			.stream()
			.map(ChallengeVideoResponse::from)
			.toList();

		return ResponseData.success("오늘의 미션 영상 조회 성공", videos);
	}

	@Override
	public ResponseData<Void> deleteVideoByUser(
		@RequestParam Long userId,
		@PathVariable Long videoId
	) {
		challengeVideoService.deleteVideoByUser(userId, videoId);
		return ResponseData.success("영상이 삭제되었습니다.", null);
	}
}
