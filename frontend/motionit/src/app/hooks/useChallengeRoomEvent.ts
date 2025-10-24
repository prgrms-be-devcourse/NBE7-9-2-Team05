'use client';

import { useStomp } from "../providers/StompProvider";
import { useEffect } from "react";
import { ChallengeRoomEvent, EventEnum } from "../../type";
import { useRoom } from "../../stores";

export function useChallengeRoomEvents() {
    const { subscribeTo } = useStomp();

    useEffect(() => {
        const off = subscribeTo<ChallengeRoomEvent>('/topic/challenge/rooms', (evt) => {
            if((evt?.event ?? '').toUpperCase() != EventEnum.ROOM) {
                return;
            }

            const { getRooms, page, size, status } = useRoom.getState();
            void getRooms(page, size, status);
        });

        return off;
    }, [subscribeTo]);
}