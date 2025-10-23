package com.back.motionit.domain.challenge.comment.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.comment.dto.CommentCreateReq;
import com.back.motionit.domain.challenge.comment.dto.CommentEditReq;
import com.back.motionit.domain.challenge.comment.dto.CommentRes;
import com.back.motionit.domain.challenge.comment.entity.Comment;
import com.back.motionit.domain.challenge.comment.moderation.CommentModeration;
import com.back.motionit.domain.challenge.comment.repository.CommentRepository;
import com.back.motionit.domain.challenge.like.repository.CommentLikeRepository;
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
	private final CommentLikeRepository commentLikeRepository;
	private final ChallengeRoomService challengeRoomService;
	private final CommentModeration commentModeration;

	@Transactional
	public CommentRes create(Long roomId, Long userId, CommentCreateReq req) {
		commentModeration.assertClean(req.content());
		ChallengeRoom room = challengeRoomRepository.findById(roomId)
			.orElseThrow(() -> new BusinessException(CommentErrorCode.ROOM_NOT_FOUND));
		User author = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(CommentErrorCode.USER_NOT_FOUND));

		Comment c = Comment.builder()
			.challengeRoom(room)
			.user(author)
			.content(req.content())
			.build();
		commentRepository.save(c);

		return CommentRes.from(c, false);
	}

	@Transactional(readOnly = true)
	public Page<CommentRes> list(Long roomId, Long userId, int page, int size) {
		if (!challengeRoomRepository.existsById(roomId)) {
			throw new BusinessException(CommentErrorCode.ROOM_NOT_FOUND);
		}

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(CommentErrorCode.USER_NOT_FOUND));

		Pageable pageable = PageRequest.of(page, size);
		Page<Comment> comments = commentRepository
			.findActiveByRoomIdWithAuthor(roomId, pageable);

		if (comments.isEmpty()) {
			return comments.map(c -> CommentRes.from(c, false));
		}

		List<Long> commentIds = comments.getContent().stream()
			.map(Comment::getId)
			.toList();

		Set<Long> likedCommentIds = commentLikeRepository
			.findLikedCommentIdsSafely(user, commentIds);

		return comments.map(c -> {
			boolean isLiked = likedCommentIds.contains(c.getId());
			return CommentRes.from(c, isLiked);
		});
	}

	@Transactional
	public CommentRes edit(Long roomId, Long commentId, Long userId, CommentEditReq req) {

		commentModeration.assertClean(req.content());

		Comment c = commentRepository.findByIdAndChallengeRoom_Id(commentId, roomId)
			.orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));

		if (!c.getUser().getId().equals(userId)) {
			throw new BusinessException(CommentErrorCode.WRONG_ACCESS);
		}

		c.edit(req.content());

		User user = userRepository.getReferenceById(userId);
		boolean isLiked = commentLikeRepository.existsByCommentAndUser(c, user);

		return CommentRes.from(c, isLiked);
	}

	@Transactional
	public CommentRes delete(Long roomId, Long commentId, Long userId) {

		Comment c = commentRepository.findByIdAndChallengeRoom_Id(commentId, roomId)
			.orElseThrow(() -> new BusinessException(CommentErrorCode.COMMENT_NOT_FOUND));

		if (!c.getUser().getId().equals(userId)) {
			throw new BusinessException(CommentErrorCode.WRONG_ACCESS);
		}

		c.softDelete();
		commentLikeRepository.deleteAllByComment(c);

		return CommentRes.from(c, false);
	}

}
