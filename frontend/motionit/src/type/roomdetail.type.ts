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
  nickname: string;
  userProfile: string;
  missionDate: string;
  completed: boolean;
  isHost: "HOST" | "MEMBER";
  aiSummary?: string | null;
}

export interface ParticipationStatus {
  userId: number;
  roomId: number;
  joined: boolean;
}

export interface Comment {
  id: number;
  roomId: number;
  authorId: number;
  authorNickname: string;
  content: string;
  deleted: boolean;
  likeCount: number;
  isLiked: boolean;
  createdAt: string;
  updatedAt: string;
}