package com.back.motionit.domain.comments.comment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentEditReq(
	@NotBlank
	@Size(max = 1000)
	String content) {
}
