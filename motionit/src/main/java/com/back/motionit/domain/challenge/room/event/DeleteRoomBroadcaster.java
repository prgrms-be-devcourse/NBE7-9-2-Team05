package com.back.motionit.domain.challenge.room.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.back.motionit.domain.challenge.room.dto.ChallengeRoomDeleted;
import com.back.motionit.global.event.Broadcaster;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeleteRoomBroadcaster implements Broadcaster<ChallengeRoomDeleted> {

	private final SimpMessagingTemplate messagingTemplate;

	@Override
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onCreated(ChallengeRoomDeleted event) {
		messagingTemplate.convertAndSend("/topic/challenge/rooms", event);
	}
}
