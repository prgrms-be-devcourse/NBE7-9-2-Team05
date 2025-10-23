export type ChallengeRoomsResponse = {
    total: number;
    rooms: RoomSummary[];
}

export type RoomSummary = {
    id: number;
    title: string;
    description: string;
    roomImage: string;
    capacity: number;
    dDay: number; // D-22 처럼 남은 일수
    status: ChallengeStatus;
    current: number;
}

export enum ChallengeStatus {
    ALL = 'ALL',
    JOINING = 'JOINING',
    JOINABLE = 'JOINABLE',
}

export enum RoomCta {
    GET_IN = '입장하기',
    JOIN = '참여하기',
}

export enum EventEnum {
    ROOM = 'ROOM',
}

export interface CreateRoomRequest {
    title: string;
    description: string;
    capacity: number;
    duration: number;
    videoUrl: string;
    imageFileName: string;
    contentType: string;
}
