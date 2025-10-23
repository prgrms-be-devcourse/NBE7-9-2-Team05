package com.back.motionit.domain.challenge.like.api;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.back.motionit.domain.challenge.comment.dto.CommentRes;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.respoonsedata.ResponseData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "CommentLike", description = "댓글 좋아요 관리")
public interface CommentLikeApi {

	@Operation(summary = "댓글 좋아요 토글", description = "댓글에 대한 좋아요를 생성하거나 취소하고, 업데이트된 댓글 정보를 반환합니다.")
	@PostMapping("/{commentId}/likes")
	@ResponseStatus(HttpStatus.OK)
	ResponseData<CommentRes> toggleCommentLikeByCommentId(
		@PathVariable Long commentId,
		@AuthenticationPrincipal User authUser
	);
}

