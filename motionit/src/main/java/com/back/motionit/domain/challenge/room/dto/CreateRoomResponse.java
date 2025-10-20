package com.back.motionit.domain.challenge.room.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.entity.OpenStatus;

public record CreateRoomResponse(
	Long id,
	Long userId,
	String title,
	String description,
	Integer capacity,
	OpenStatus openStatus,
	LocalDateTime challengeStartDate,
	LocalDateTime challengeEndDate,
	String roomImage,
	List<ChallengeVideo> challengeVideoList
) {
	public CreateRoomResponse(ChallengeRoom room) {
		this(
			room.getId(),
			room.getUser().getId(),
			room.getTitle(),
			room.getDescription(),
			room.getCapacity(),
			room.getOpenStatus(),
			room.getChallengeStartDate(),
			room.getChallengeEndDate(),
			room.getRoomImage(),
			room.getChallengeVideoList()
		);
	}
}
