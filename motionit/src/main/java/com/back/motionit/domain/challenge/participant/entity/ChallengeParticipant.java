package com.back.motionit.domain.challenge.participant.entity;

import java.time.LocalDateTime;

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
@Table(name = "challenge_participants")
public class ChallengeParticipant extends BaseEntity {
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;  // 챌린지 참가자

	@ManyToOne(fetch = FetchType.LAZY)
	private ChallengeRoom challengeRoom;

	@Column(name = "quit_date")
	private LocalDateTime quitDate; // 챌린지 참가자가 챌린지를 그만둔 날짜
	@Column(name = "is_active")
	private Boolean isActive = true; // 챌린지 참가자의 활동 상태 (예: 활동 중, 비활동 중 등)

	@Column(nullable = false)
	private ChallengeParticipantRole role; // 챌린지 참가자의 역할 (예: NORMAL, ADMIN)

	@Column(nullable = false, name = "challenge_status")
	private Boolean challengeStatus = false; // 챌린지 참가자의 챌린지 상태 (예: 진행 중, 완료 등)

	// TODO: 불리안 타입의 challengeStatus은 오늘 완료와 내일 미완료 구분을 못함 추후 별도 엔티티로 관리 필요

	public ChallengeParticipant(User user, ChallengeRoom challengeRoom,
		ChallengeParticipantRole challengeParticipantRole) {
		this.user = user;
		this.challengeRoom = challengeRoom;
		this.isActive = true;
		this.role = challengeParticipantRole;
	}

	public void quitChallenge() {
		this.isActive = false;
		this.quitDate = LocalDateTime.now();
	}
}
