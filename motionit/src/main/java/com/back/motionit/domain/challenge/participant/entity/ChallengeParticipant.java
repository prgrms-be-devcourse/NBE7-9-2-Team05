package com.back.motionit.domain.challenge.participant.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
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
@Table(name = "challenge_participants")
public class ChallengeParticipant extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;  // 챌린지 참가자

	@ManyToOne(fetch = FetchType.LAZY)
	private ChallengeRoom challengeRoom;

	@Column(name = "quit_date")
	private LocalDateTime quitDate; // 챌린지 참가자가 챌린지를 그만둔 날짜

	@Column(name = "roome_image")
	private String roomImage; // 챌린지룸 이미지 URL

	@OneToMany
	private List<ChallengeVideo> challengeVideoList = new ArrayList<>();
}
