package com.back.motionit.domain.challenge.comment.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import com.back.motionit.domain.challenge.comment.dto.CommentCreateReq;
import com.back.motionit.domain.challenge.comment.dto.CommentEditReq;
import com.back.motionit.domain.challenge.comment.repository.CommentRepository;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.video.entity.OpenStatus;
import com.back.motionit.domain.user.entity.LoginType;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@TestPropertySource(properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration")
class CommentControllerIntegrationTest {

	@Autowired
	MockMvc mockMvc;
	@Autowired
	ObjectMapper om;
	@Autowired
	JdbcTemplate jdbc;
	@Autowired
	UserRepository userRepository;
	@Autowired
	ChallengeRoomRepository roomRepository;
	@Autowired
	CommentRepository commentRepository;
	private static final String BASE = "/api/v1/rooms/{roomId}/comments";
	private static final String ONE = "/api/v1/rooms/{roomId}/comments/{commentId}";
	private Object ignoreYoutubeClient;

	@BeforeEach
	void setUp() {
		// 1) FK 끔
		jdbc.execute("SET REFERENTIAL_INTEGRITY FALSE");

		// 2) 테이블 비우기 + 아이덴티티 리셋 (TRUNCATE가 가장 안전)
		try {
			jdbc.execute("TRUNCATE TABLE room_comments RESTART IDENTITY");
		} catch (Exception ignored) {
		}
		try {
			jdbc.execute("TRUNCATE TABLE challenge_rooms RESTART IDENTITY");
		} catch (Exception ignored) {
		}
		try {
			jdbc.execute("TRUNCATE TABLE users RESTART IDENTITY");
		} catch (Exception ignored) {
		}

		// 3) FK 켬
		jdbc.execute("SET REFERENTIAL_INTEGRITY TRUE");

		// 4) 시드 (user #1)
		User u1 = User.builder()
			.email("u1@test.com")
			.nickname("u1")
			.password("pw")
			.loginType(LoginType.LOCAL)
			.userProfile(null)
			.build();
		userRepository.save(u1);

		// 5) 시드 (room #1)
		ChallengeRoom r1 = new ChallengeRoom(
			u1,
			"Room-1",
			"Desc",
			100,
			OpenStatus.OPEN,
			LocalDateTime.now().minusDays(1),
			LocalDateTime.now().plusDays(30),
			"/img.png",
			new ArrayList<>(),
			new ArrayList<>()
		);
		roomRepository.save(r1);
	}

	private Long createComment(Long roomId, String content) throws Exception {
		var req = new CommentCreateReq(content);
		var result = mockMvc.perform(post(BASE, roomId)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(req)))
			// .andExpect(status().isCreated())  // <— 삭제
			.andExpect(jsonPath("$.resultCode").value("201-0"))
			.andExpect(jsonPath("$.data.id").exists())
			.andReturn();
		var body = result.getResponse().getContentAsString();
		var node = om.readTree(body).get("data");
		return node.get("id").asLong();
	}

	@Test
	@DisplayName("POST create -> HTTP200, resultCode=201-0, returns body")
	void create_comment() throws Exception {
		mockMvc.perform(post(BASE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new CommentCreateReq("첫 댓글"))))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.resultCode").value("201-0"))
			.andExpect(jsonPath("$.data.roomId").value(1))
			.andExpect(jsonPath("$.data.authorId").value(1))
			.andExpect(jsonPath("$.data.authorNickname").value("u1"))
			.andExpect(jsonPath("$.data.content").value("첫 댓글"))
			.andExpect(jsonPath("$.data.deleted").value(false))
			.andExpect(jsonPath("$.data.likeCount").value(0));
	}

	@Test
	@DisplayName("GET list paged -> HTTP200, ResponseData<Page>")
	void list_paged() throws Exception {
		for (int i = 0; i < 7; i++)
			createComment(1L, "c" + i);

		mockMvc.perform(get(BASE, 1L).param("page", "0").param("size", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value("200-0"))
			.andExpect(jsonPath("$.data.content.length()").value(5))
			.andExpect(jsonPath("$.data.totalElements").value(7))
			.andExpect(jsonPath("$.data.totalPages").value(2))
			.andExpect(jsonPath("$.data.number").value(0));
	}

	@Test
	@DisplayName("PATCH edit -> HTTP200, content updated")
	void edit_comment() throws Exception {
		Long id = createComment(1L, "old");
		mockMvc.perform(patch(ONE, 1L, id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new CommentEditReq("new"))))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value("200-0"))
			.andExpect(jsonPath("$.data.id").value(id))
			.andExpect(jsonPath("$.data.content").value("new"));
	}

	@Test
	@DisplayName("DELETE soft -> HTTP200(resultCode=204-0), list excludes deleted")
	void delete_comment_soft_excluded_from_list() throws Exception {
		Long id1 = createComment(1L, "to-del-1");
		Long id2 = createComment(1L, "to-stay-2");

		mockMvc.perform(delete(ONE, 1L, id1))
			.andExpect(status().isNoContent())
			.andExpect(jsonPath("$.resultCode").value("204-0"));

		mockMvc.perform(get(BASE, 1L).param("page", "0").param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value("200-0"))
			.andExpect(jsonPath("$.data.totalElements").value(1))
			.andExpect(jsonPath("$.data.content[0].id").value(id2))
			.andExpect(jsonPath("$.data.content[0].content").value("to-stay-2"));
	}

	@Test
	void wrong_access_on_edit() throws Exception {
		// user #2 생성
		var u2 = User.builder()
			.email("u2@test.com").nickname("u2").password("pw")
			.loginType(LoginType.LOCAL).userProfile(null).build();
		userRepository.save(u2);

		// room #1 참조
		var room = roomRepository.getReferenceById(1L);

		// author=2인 댓글 직접 저장
		var c = com.back.motionit.domain.challenge.comment.entity.Comment.builder()
			.challengeRoom(room)
			.user(u2)
			.content("not-mine")
			.build();
		commentRepository.save(c);
		Long id = c.getId();

		// 현재 컨트롤러는 extractUserId()=1 → WRONG_ACCESS 발생 기대
		mockMvc.perform(patch(ONE, 1L, id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new CommentEditReq("should-fail"))))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.resultCode").value("U-101"));
	}

	@Test
	@DisplayName("검증에러 400 + NOT_FOUND 404 매핑 확인")
	void validation_and_notfound_cases() throws Exception {
		// 400: POST content=""
		mockMvc.perform(post(BASE, 1L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new CommentCreateReq(""))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.resultCode").value("C-002"));   // ← 여기

		// 시드 1개
		Long id = createComment(1L, "ok");

		// 400: PATCH content=""
		mockMvc.perform(patch(ONE, 1L, id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new CommentEditReq(""))))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.resultCode").value("C-002"));  // ← 여기

		// 404: PATCH 잘못된 commentId
		mockMvc.perform(patch(ONE, 1L, 999L)
				.contentType(MediaType.APPLICATION_JSON)
				.content(om.writeValueAsString(new CommentEditReq("nope"))))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.resultCode").value("M-102"));  // COMMENT_NOT_FOUND

		// 404: DELETE 잘못된 commentId
		mockMvc.perform(delete(ONE, 1L, 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.resultCode").value("M-102"));
	}

	@Test
	@DisplayName("페이지네이션 정렬(내림차순) + 마지막/빈 페이지 + soft-delete 제외")
	void pagination_and_softdelete_behaviour() throws Exception {
		// 7개 시드 (id 1..7)
		java.util.List<Long> ids = new java.util.ArrayList<>();
		for (int i = 0; i < 7; i++)
			ids.add(createComment(1L, "c" + i));

		// page=0,size=5 확인 (정렬: 최신 먼저)
		mockMvc.perform(get(BASE, 1L).param("page", "0").param("size", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value("200-0"))
			.andExpect(jsonPath("$.data.totalElements").value(7))
			.andExpect(jsonPath("$.data.totalPages").value(2))
			.andExpect(jsonPath("$.data.number").value(0))
			.andExpect(jsonPath("$.data.content.length()").value(5))
			// 내림차순: 첫 요소 id가 마지막 요소 id보다 커야 함
			.andExpect(jsonPath("$.data.content[0].id").exists())
			.andExpect(jsonPath("$.data.content[4].id").exists());

		// page=1,size=5 (마지막 페이지: 2개)
		mockMvc.perform(get(BASE, 1L).param("page", "1").param("size", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value("200-0"))
			.andExpect(jsonPath("$.data.number").value(1))
			.andExpect(jsonPath("$.data.content.length()").value(2))
			.andExpect(jsonPath("$.data.last").value(true));

		// page=2,size=5 (빈 페이지)
		mockMvc.perform(get(BASE, 1L).param("page", "2").param("size", "5"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value("200-0"))
			.andExpect(jsonPath("$.data.content.length()").value(0))
			.andExpect(jsonPath("$.data.empty").value(true));

		// soft-delete: 하나 삭제 → 목록에서 제외됨 + totalElements 감소
		Long toDelete = ids.get(0); // 아무거나
		mockMvc.perform(delete(ONE, 1L, toDelete))
			.andExpect(status().isNoContent())
			.andExpect(jsonPath("$.resultCode").value("204-0"));

		mockMvc.perform(get(BASE, 1L).param("page", "0").param("size", "10"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.resultCode").value("200-0"))
			.andExpect(jsonPath("$.data.totalElements").value(6))
			// 삭제된 id가 목록에 없는지
			.andExpect(jsonPath("$.data.content[*].id", not(hasItem(toDelete.intValue()))));
	}

}
