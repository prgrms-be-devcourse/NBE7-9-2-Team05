package com.back.motionit.domain.challenge.video.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ChallengeVideoUploadRequest(
	@NotNull Long userId,
	@NotBlank String youtubeUrl
) {
}
