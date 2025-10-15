package com.back.motionit.domain.challenge.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.back.motionit.domain.challenge.comment.profanityfilter.ProfanityFilter;
import com.back.motionit.domain.challenge.comment.repository.CommentRepository;
import com.back.motionit.domain.challenge.room.repository.ChallengeRoomRepository;
import com.back.motionit.domain.challenge.room.service.ChallengeRoomService;
import com.back.motionit.domain.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;
	private final ChallengeRoomRepository challengeRoomRepository;
	private final UserRepository userRepository;
	private final ChallengeRoomService challengeRoomService;
	private final ProfanityFilter profanityFilter;

	@Transactional
	public Comentres
	challengeRoomser

}
