package com.back.motionit.domain.challenge.comment.event;

import com.back.motionit.domain.challenge.comment.dto.CommentRes;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentEventDto {
	public enum Type { CREATED, UPDATED, DELETED }
	private final Long roomId;
	private final Type type;
	private final CommentRes payload;
}