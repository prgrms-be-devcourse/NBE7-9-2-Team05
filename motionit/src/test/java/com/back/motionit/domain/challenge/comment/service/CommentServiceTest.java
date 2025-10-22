package com.back.motionit.domain.challenge.comment.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.back.motionit.domain.challenge.comment.dto.CommentCreateReq;
import com.back.motionit.domain.challenge.comment.dto.CommentEditReq;
import com.back.motionit.domain.challenge.comment.entity.Comment;
import com.back.motionit.domain.challenge.comment.moderation.CommentModeration;
import com.back.motionit.domain.challenge.comment.repository.CommentRepository;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.room.service.ChallengeRoomService;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.error.exception.BusinessException;

class CommentServiceTest {

	CommentRepository commentRepository = mock(CommentRepository.class);
	ChallengeRoomRepository challengeRoomRepository = mock(ChallengeRoomRepository.class);
	UserRepository userRepository = mock(UserRepository.class);
	ChallengeRoomService challengeRoomService = mock(ChallengeRoomService.class);
	CommentModeration commentModeration = new CommentModeration();

	CommentService service;

	@BeforeEach
	void setUp() {
		service = new CommentService(
			commentRepository, challengeRoomRepository, userRepository, challengeRoomService, commentModeration
		);
	}

	@Test
	@DisplayName("create: BLOCK이면 저장 로직까지 가지 않고 예외")
	void create_block_stops_early() {
		Long roomId = 1L; Long userId = 1L;


		ChallengeRoom room = mock(ChallengeRoom.class);
		User author = mock(User.class);

		when(challengeRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
		when(userRepository.findById(userId)).thenReturn(Optional.of(author));

		var req = new CommentCreateReq("움직임이 너무 븅1신같으세요ㅋㅋ"); // BLOCK

		assertThatThrownBy(() -> service.create(roomId, userId, req))
			.isInstanceOf(BusinessException.class);


		verify(commentRepository, never()).save(any(Comment.class));
	}

	@Test
	@DisplayName("create: ALLOW면 정상 저장 로직 호출")
	void create_allow_saves() {
		Long roomId = 1L; Long userId = 1L;

		ChallengeRoom room = mock(ChallengeRoom.class);
		User author = mock(User.class);

		when(challengeRoomRepository.findById(roomId)).thenReturn(Optional.of(room));
		when(userRepository.findById(userId)).thenReturn(Optional.of(author));

		when(commentRepository.save(any(Comment.class))).thenAnswer(inv -> inv.getArgument(0));

		var req = new CommentCreateReq("2012년의 교통사고가 허리디스크 문제의 시발점이었습니다."); // ALLOW

		service.create(roomId, userId, req);

		verify(commentRepository, times(1)).save(any(Comment.class));
	}

	@Test
	@DisplayName("edit: 작성자 본인 + WARN이면 예외 발생, 내용 변경 안 됨")
	void edit_warn_throws() {
		Long roomId = 10L; Long commentId = 20L; Long userId = 99L;


		User author = mock(User.class);
		when(author.getId()).thenReturn(userId);

		ChallengeRoom room = mock(ChallengeRoom.class);

		Comment comment = mock(Comment.class);
		when(comment.getUser()).thenReturn(author);
		when(comment.getChallengeRoom()).thenReturn(room);

		when(commentRepository.findByIdAndChallengeRoom_Id(commentId, roomId))
			.thenReturn(Optional.of(comment));

		var req = new CommentEditReq("말을 왜 그렇게 하세요? 정신병자세요?"); // WARN

		assertThatThrownBy(() -> service.edit(roomId, commentId, userId, req))
			.isInstanceOf(BusinessException.class);


		verify(comment, never()).edit(anyString());
	}

	@Test
	@DisplayName("edit: 작성자 본인 + ALLOW면 내용 변경 호출")
	void edit_allow_updates() {
		Long roomId = 10L; Long commentId = 20L; Long userId = 99L;

		User author = mock(User.class);
		when(author.getId()).thenReturn(userId);

		ChallengeRoom room = mock(ChallengeRoom.class);

		Comment comment = mock(Comment.class);
		when(comment.getUser()).thenReturn(author);
		when(comment.getChallengeRoom()).thenReturn(room);

		when(commentRepository.findByIdAndChallengeRoom_Id(commentId, roomId))
			.thenReturn(Optional.of(comment));

		var req = new CommentEditReq("병신도라는 불교용어가 있습니다."); // ALLOW

		service.edit(roomId, commentId, userId, req);

		verify(comment, times(1)).edit("병신도라는 불교용어가 있습니다.");
	}
}
