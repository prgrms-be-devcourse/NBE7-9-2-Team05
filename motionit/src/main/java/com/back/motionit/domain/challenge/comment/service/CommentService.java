package com.back.motionit.domain.challenge.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.comment.dto.CommentCreateReq;
import com.back.motionit.domain.challenge.comment.dto.CommentEditReq;
import com.back.motionit.domain.challenge.comment.dto.CommentRes;
import com.back.motionit.domain.challenge.comment.entity.Comment;
import com.back.motionit.domain.challenge.comment.profanityfilter.ProfanityFilter;
import com.back.motionit.domain.challenge.comment.repository.CommentRepository;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.room.service.ChallengeRoomService;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.error.code.CommentErrorCode;
import com.back.motionit.global.error.exception.BusinessException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final ChallengeRoomRepository challengeRoomRepository;
	private final UserRepository userRepository;
	private final ChallengeRoomService challengeRoomService;
//	private final ProfanityFilter profanityFilter;

	@Transactional
	public CommentRes create(Long roomId, Long userId, CommentCreateReq req) {

		ChallengeRoom room = challengeRoomRepository.getReferenceById(roomId);
		User author = userRepository.getReferenceById(userId);

		Comment c = Comment.builder()
			.challengeRoom(room)
			.user(author)
			.content(req.content())
			.build();
		commentRepository.save(c);
		return CommentRes.from(c);
	}

	@Transactional(readOnly = true)
	public Page<CommentRes> list(Long roomId, Long userId, int page, int size) {

		Pageable pageable = PageRequest.of(page, size);
		return commentRepository.findActiveByRoomIdWithAuthor(roomId, pageable)
			.map(CommentRes::from);
	}

	@Transactional
	public CommentRes edit(Long roomId, Long commentId, Long userId, CommentEditReq req) {

		Comment c = commentRepository.findByIdAndChallengeRoom_Id(commentId, roomId)
			.orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));

		// 작성자 본인만
		if (!c.getUser().getId().equals(userId)) {
			throw new BusinessException(CommentErrorCode.WRONG_ACCESS);
		}

		c.edit(req.content());
		return CommentRes.from(c);
	}

	@Transactional
	public void delete(Long roomId, Long commentId, Long userId) {

		Comment c = commentRepository.findByIdAndChallengeRoom_Id(commentId, roomId)
			.orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));
		if (!c.getUser().getId().equals(userId)) {
			throw new BusinessException(CommentErrorCode.WRONG_ACCESS);
		}

		c.softDelete();
	}

}
