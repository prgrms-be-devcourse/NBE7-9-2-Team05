package com.back.motionit.domain.challenge.video.external.youtube;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.back.motionit.domain.challenge.video.external.youtube.dto.YoutubeVideoMetadata;

@Component
@Profile("perf") // perf 프로필에서만 활성화됨
public class YoutubeMetadataClientStub extends YoutubeMetadataClient {

	@Override
	public YoutubeVideoMetadata fetchMetadata(String youtubeUrl) {
		// 실제 API 호출 대신 고정된 더미 메타데이터 반환 (성능 테스트용)
		return YoutubeVideoMetadata.builder()
			.videoId("dummy-video-id")
			.title("Performance Test Video")
			.thumbnailUrl("https://img.youtube.com/vi/dummy/hqdefault.jpg")
			.durationSeconds(120)
			.build();
	}
}