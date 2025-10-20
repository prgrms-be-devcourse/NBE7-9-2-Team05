package com.back.motionit.domain.challenge.mission.entity;

import java.time.LocalDate;

import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.global.jpa.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(
	name = "challenge_mission_status",
	uniqueConstraints = {
		@UniqueConstraint(columnNames = {"participant_id", "mission_date", "video_id"})
	}
)
public class ChallengeMissionStatus extends BaseEntity {
	// 어떤 참가자의 기록인지
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "participant_id", nullable = false)
	private ChallengeParticipant participant;

	// 어떤 영상에 대한 기록인지 (optional — 하루 1영상일 경우 null 가능)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "video_id")
	private ChallengeVideo video;

	// 해당 미션 날짜
	@Column(name = "mission_date", nullable = false)
	private LocalDate missionDate;

	// 완료 여부
	@Column(nullable = false)
	private Boolean completed;

	// 미션 완료 처리 메서드
	public void completeMission(ChallengeVideo video) {
		this.completed = true;
		this.video = video;
	}
}
