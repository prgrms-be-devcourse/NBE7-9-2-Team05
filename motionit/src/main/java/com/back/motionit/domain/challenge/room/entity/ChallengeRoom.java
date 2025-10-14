package com.back.motionit.domain.challenge.room.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.entity.OpenStatus;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table(name = "challenge_rooms")
public class ChallengeRoom extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	private User user;

	private String title;
	private String description;
	private Integer capacity;
	private OpenStatus openStatus;

	@Column(name = "challenge_start_date")
	private LocalDateTime challengeStartDate;

	@Column(name = "challenge_end_date")
	private LocalDateTime challengeEndDate;

	@Column(name = "roome_image")
	private String roomImage; // 챌린지룸 이미지 URL

	@OneToMany
	private List<ChallengeVideo> challengeVideoList = new ArrayList<>();
}
