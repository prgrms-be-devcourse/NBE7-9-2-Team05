package com.back.motionit.domain.challenge.comment.moderation;

import org.springframework.stereotype.Component;

import com.back.motionit.global.error.code.CommentErrorCode;
import com.back.motionit.global.error.exception.BusinessException;

@Component
public class CommentModeration {

	public void assertClean(String content) {
		KeywordFilter.Decision d = KeywordFilter.decide(content);

		switch (d) {
			case BLOCK -> throw new BusinessException(CommentErrorCode.INAPPROPRIATE_CONTENT_BLOCK);
			case WARN  -> throw new BusinessException(CommentErrorCode.INAPPROPRIATE_CONTENT_WARN);
			case ALLOW -> { /* 통과 */ }
		}
	}
}