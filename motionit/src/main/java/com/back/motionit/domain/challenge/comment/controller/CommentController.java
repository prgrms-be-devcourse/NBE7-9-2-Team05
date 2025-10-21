package com.back.motionit.domain.challenge.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.back.motionit.domain.challenge.comment.dto.CommentCreateReq;
import com.back.motionit.domain.challenge.comment.dto.CommentEditReq;
import com.back.motionit.domain.challenge.comment.dto.CommentRes;
import com.back.motionit.domain.challenge.comment.service.CommentService;
import com.back.motionit.global.respoonsedata.ResponseData;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Comment API", description = "운동방 댓글 CRUD 기능")
@RestController
@RequestMapping("/api/v1/rooms/{roomId}/comments")
@RequiredArgsConstructor

public class CommentController {

	private final CommentService commentService;

	@Operation(summary = "댓글 생성", description = "운동방에 댓글을 작성합니다.")
	@PostMapping
	public ResponseData<CommentRes> create(@PathVariable Long roomId,
		@AuthenticationPrincipal Object authUser,
		@Valid @RequestBody CommentCreateReq req) {
		Long userId = extractUserId(authUser);
		CommentRes res = commentService.create(roomId, userId, req);
		return ResponseData.success("201-0", "created", res);
	}

	@Operation(summary = "댓글 목록 조회", description = "운동방 댓글을 페이지 단위로 조회합니다.")
	@GetMapping
	public ResponseData<Page<CommentRes>> list(@PathVariable Long roomId,
		@AuthenticationPrincipal Object authUser,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "20") int size) {
		Long userId = extractUserId(authUser);
		Page<CommentRes> resPage = commentService.list(roomId, userId, page, size);
		return ResponseData.success("200-0", "success", resPage);
	}

	@Operation(summary = "댓글 수정", description = "작성자가 본인의 댓글을 수정합니다.")
	@PatchMapping("/{commentId}")
	public ResponseData<CommentRes> edit(@PathVariable Long roomId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal Object authUser,
		@Valid @RequestBody CommentEditReq req) {
		Long userId = extractUserId(authUser);
		CommentRes res = commentService.edit(roomId, commentId, userId, req);
		return ResponseData.success("200-0", "updated", res);
	}

	@Operation(summary = "댓글 삭제", description = "작성자가 본인의 댓글을 삭제합니다.")
	@DeleteMapping("/{commentId}")
	public ResponseData<Void> delete(@PathVariable Long roomId,
		@PathVariable Long commentId,
		@AuthenticationPrincipal Object authUser) {
		Long userId = extractUserId(authUser);
		commentService.delete(roomId, commentId, userId);
		return ResponseData.success("204-0", "deleted", null);
	}

	// ------------------- 임시 유저ID 추출 (로그인 붙이면 교체 예정) -------------------
	private Long extractUserId(Object authUser) {

		return 1L;
	}
}
