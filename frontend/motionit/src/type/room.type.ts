export type Challenge = {
    id: number;
    title: string;
    description: string;
    imageUrl: string;
    current: number;
    capacity: number;
    dday: number; // D-22 처럼 남은 일수
    status: ChallengeStatus;
    cta: RoomCta;
}

export enum ChallengeStatus {
    ALL = '전체',
    JOINING = '참여중',
    JOINABLE = '참여가능',
}

export enum RoomCta {
    GET_IN = '입장하기',
    JOIN = '참여하기',
}

