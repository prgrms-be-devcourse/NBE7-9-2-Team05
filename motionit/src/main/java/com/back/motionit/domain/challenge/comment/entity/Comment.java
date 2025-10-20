package com.back.motionit.domain.challenge.comment.entity;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
	name = "room_comments",
	indexes = {
		@Index(name = "idx_room_comments_room_created", columnList = "room_id, create_date DESC"),
		@Index(name = "idx_room_comments_author", columnList = "author_id")
	}
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class Comment extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "room_id", nullable = false)
	private ChallengeRoom challengeRoom;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "author_id", nullable = false)
	private User user;

	@Column(nullable = false, length = 1000)
	private String content;

	@Builder.Default
	@Column(name = "is_deleted", nullable = false)
	private boolean deleted=false;

	@Builder.Default
	@Column(name = "like_count", nullable = false, columnDefinition = "INT DEFAULT 0")
	private Integer likeCount=0;

	public void edit(String newContent) {
		this.content = newContent;
	}

	public void softDelete() {
		this.deleted = true;
		this.content = "삭제된 댓글입니다";
	}

	public void restore() {
		this.deleted = false;
	}
}
