package com.back.motionit.domain.challenge.room.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipant;
import com.back.motionit.domain.challenge.participant.entity.ChallengeParticipantRole;
import com.back.motionit.domain.challenge.participant.service.ChallengeParticipantService;
import com.back.motionit.domain.challenge.room.dto.ChallengeRoomCreated;
import com.back.motionit.domain.challenge.room.dto.CreateRoomRequest;
import com.back.motionit.domain.challenge.room.dto.CreateRoomResponse;
import com.back.motionit.domain.challenge.room.dto.GetRoomResponse;
import com.back.motionit.domain.challenge.room.dto.GetRoomsResponse;
import com.back.motionit.domain.challenge.room.entity.ChallengeRoom;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.video.entity.ChallengeVideo;
import com.back.motionit.domain.challenge.video.entity.OpenStatus;
import com.back.motionit.domain.user.entity.User;
import com.back.motionit.domain.user.repository.UserRepository;
import com.back.motionit.global.enums.EventEnums;
import com.back.motionit.global.error.code.ChallengeRoomErrorCode;
import com.back.motionit.global.error.exception.BusinessException;
import com.back.motionit.global.event.EventPublisher;
import com.back.motionit.global.service.AwsS3Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChallengeRoomService {

	private final ChallengeRoomRepository challengeRoomRepository;
	private final ChallengeParticipantService challengeParticipantService;
	private final AwsS3Service s3Service;
	private final EventPublisher eventPublisher;
	private final UserRepository userRepository;

	@Transactional
	public CreateRoomResponse createRoom(CreateRoomRequest input, User user) {
		if (user == null) {
			throw new BusinessException(ChallengeRoomErrorCode.NOT_FOUND_USER);
		}

		User host = userRepository.findById(user.getId())
			.orElseThrow(() -> new BusinessException(ChallengeRoomErrorCode.NOT_FOUND_USER));

		String objectKey = s3Service.buildObjectKey(input.imageFileName());
		ChallengeRoom room = mapToRoomObject(input, host, objectKey);
		ChallengeRoom createdRoom = challengeRoomRepository.save(room);

		// 방장 자동 참가 처리, 여기서 실패시 방 생성도 롤백 처리됨
		autoJoinAsHost(createdRoom);

		String url = s3Service.createUploadUrl(
			objectKey,
			input.contentType()
		);

		CreateRoomResponse response = mapToCreateRoomResponse(createdRoom, url);
		eventPublisher.publishEvent(new ChallengeRoomCreated(EventEnums.ROOM, response.id()));

		return response;
	}

	@Transactional
	public List<GetRoomsResponse> getRooms(int page, int size) {
		Pageable pageable = PageRequest.of(
			page,
			size,
			Sort.by(Sort.Direction.DESC, "createDate")
		);

		return challengeRoomRepository
			.findByOpenStatus(OpenStatus.OPEN, pageable)
			.map(this::mapToGetRoomsResponse)
			.getContent();
	}

	@Transactional
	public GetRoomResponse getRoom(Long roomId) {
		ChallengeRoom room = challengeRoomRepository.findById(roomId).orElseThrow(
			() -> new BusinessException(ChallengeRoomErrorCode.NOT_FOUND_ROOM)
		);

		return mapToGetRoomResponse(room);
	}

	public ChallengeRoom mapToRoomObject(CreateRoomRequest input, User user, String objectKey) {
		LocalDateTime now = LocalDateTime.now();
		int durationDays = input.duration();

		LocalDateTime start = now;
		LocalDateTime end = start.plusDays(durationDays);

		List<ChallengeVideo> videos = new ArrayList<>();
		List<ChallengeParticipant> participants = new ArrayList<>();

		return new ChallengeRoom(
			user,
			input.title(),
			input.description(),
			input.capacity(),
			OpenStatus.OPEN,
			start,
			end,
			objectKey,
			videos,
			participants
		);
	}

	private CreateRoomResponse mapToCreateRoomResponse(ChallengeRoom room, String uploadUrl) {
		return new CreateRoomResponse(
			room.getId(),
			room.getTitle(),
			room.getDescription(),
			room.getCapacity(),
			room.getOpenStatus(),
			room.getChallengeStartDate(),
			room.getChallengeEndDate(),
			room.getRoomImage(),
			room.getChallengeVideoList(),
			uploadUrl
		);
	}

	private GetRoomsResponse mapToGetRoomsResponse(ChallengeRoom room) {
		return new GetRoomsResponse(
			room.getTitle(),
			room.getDescription(),
			room.getCapacity(),
			(int)room.getDDay(),
			room.getRoomImage()
		);
	}

	private GetRoomResponse mapToGetRoomResponse(ChallengeRoom room) {
		return new GetRoomResponse(
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

	private void autoJoinAsHost(ChallengeRoom createdRoom) {
		challengeParticipantService.joinChallengeRoom(
			createdRoom.getUser().getId(),
			createdRoom.getId(),
			ChallengeParticipantRole.HOST
		);
	}
}
