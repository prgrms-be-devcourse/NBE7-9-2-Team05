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
  userId?: number;
  aiMessage?: string | null;
  aiSummary?: string | null;
}

// 미션 수행 상태만 받음
export interface ParticipationStatus {
  userId: number;
  roomId: number;
  joined: boolean;
  participantId?: number;
  aiMessage?: string | null;
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

export interface ChallengeParticipant {
  participantId: number;
  nickname: string;
  userProfile: string;
  role: "HOST" | "MEMBER";
}

export interface ChallengeRoomDetail {
  id: number;
  title: string;
  description: string;
  capacity: number;
  openStatus: string;
  challengeStartDate: string;
  challengeEndDate: string;
  roomImage: string;
  videos: ChallengeVideo[];
  participants: ChallengeParticipant[];
}
