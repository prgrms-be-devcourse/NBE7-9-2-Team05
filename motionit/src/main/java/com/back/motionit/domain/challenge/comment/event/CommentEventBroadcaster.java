package com.back.motionit.domain.challenge.comment.event;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.back.motionit.global.event.Broadcaster;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CommentEventBroadcaster implements Broadcaster<CommentEventDto> {

	private final SimpMessagingTemplate messagingTemplate;

	@Override
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onCreated(CommentEventDto event) {
		String dest = "/topic/rooms/" + event.getRoomId() + "/comments";
		messagingTemplate.convertAndSend(dest, event);
	}
}