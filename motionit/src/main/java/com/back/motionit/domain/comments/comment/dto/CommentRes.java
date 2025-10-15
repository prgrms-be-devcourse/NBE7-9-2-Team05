package com.back.motionit.domain.comments.comment.dto;

import java.time.LocalDateTime;

import com.back.motionit.domain.comments.comment.entity.Comment;

public record CommentRes(
	Long id,
	Long roomId,
	Long authorId,
	String authorNickname,
	String content,
	boolean deleted,
	Integer likeCount,
	LocalDateTime createdAt,
	LocalDateTime updatedAt
) {
	public static CommentRes from(Comment c) {
		return new CommentRes(
			c.getId(),
			c.getChallengeRoom().getId(),
			c.getUser().getId(),
			c.getUser().getNickname(),
			c.isDeleted() ? "삭제된 댓글입니다" : c.getContent(),
			c.isDeleted(),
			c.getLikeCount(),
			c.getCreateDate(),
			c.getModifyDate()
		);
	}
}
