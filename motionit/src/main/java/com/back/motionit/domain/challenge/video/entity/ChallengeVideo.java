package com.back.motionit.domain.challenge.video.entity;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "challenge_videos")
public class ChallengeVideo extends BaseEntity {
	@Column(unique = true, name = "youtube_video_id")
	private String youtubeVideoId;

	@ManyToOne(fetch = FetchType.LAZY)
	private ChallengeRoom challengeRoom;

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
}
