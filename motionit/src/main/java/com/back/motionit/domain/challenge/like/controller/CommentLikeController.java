package com.back.motionit.domain.challenge.like.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.comment.dto.CommentRes;
import com.back.motionit.domain.challenge.like.api.CommentLikeApi;
import com.back.motionit.domain.challenge.like.service.CommentLikeService;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.respoonsedata.ResponseData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentLikeController implements CommentLikeApi {

	private final CommentLikeService commentLikeService;

	@Override
	public ResponseData<CommentRes> toggleCommentLikeByCommentId(
		@PathVariable Long commentId,
		@AuthenticationPrincipal User authUser
	) {
		CommentRes updatedComment = commentLikeService.toggleCommentLikeByCommentId(commentId, authUser.getId());
		String responseMessage = updatedComment.isLiked() ? "좋아요 성공" : "좋아요 취소 성공";
		return ResponseData.success("200-0", responseMessage, updatedComment);
	}
}
