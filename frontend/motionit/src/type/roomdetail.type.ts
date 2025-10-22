export interface ChallengeVideo {
    id: number;
    youtubeVideoId: string;
    title: string;
    thumbnailUrl: string;
    duration: number;
    uploadDate: string;
    isTodayMission: boolean;
    uploaderId: number;
    roomId: number;
}

export interface ChallengeMissionStatus {
  participantId: number;
  missionDate: string;
  completed: boolean;
  isHost: "HOST" | "MEMBER";
}

export interface ParticipationStatus {
  userId: number;
  roomId: number;
  joined: boolean;
}