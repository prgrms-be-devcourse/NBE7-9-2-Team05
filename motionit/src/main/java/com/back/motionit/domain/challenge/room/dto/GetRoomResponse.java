package com.back.motionit.domain.challenge.room.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.entity.OpenStatus;

public record GetRoomResponse(
	Long id,
	String title,
	String description,
	int capacity,
	OpenStatus openStatus,
	LocalDateTime challengeStartDate,
	LocalDateTime challengeEndDate,
	String roomImage,
	List<ChallengeVideo> challengeVideoList,
	List<ChallengeParticipant> participants
) {
	public GetRoomResponse(ChallengeRoom room) {
		this(
			room.getId(),
			room.getTitle(),
			room.getDescription(),
			room.getCapacity(),
			room.getOpenStatus(),
			room.getChallengeStartDate(),
			room.getChallengeEndDate(),
			room.getRoomImage(),
			room.getChallengeVideoList(),
			room.getParticipants()
		);
	}
}
