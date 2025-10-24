import { EventEnum } from "./room.type";

export type ChallengeRoomEvent = {
    event: EventEnum;
    roomId?: number;
}