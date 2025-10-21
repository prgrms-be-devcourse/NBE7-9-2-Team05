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