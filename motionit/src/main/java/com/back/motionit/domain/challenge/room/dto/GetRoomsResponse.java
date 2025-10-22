package com.back.motionit.domain.challenge.room.dto;

public record GetRoomsResponse(
	String title,
	String description,
	int capacity,
	int dDay,
	String roomImage
) {
}
