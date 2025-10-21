import { CHALLENGE_API } from "../constants";
import { fetchApi } from "./client";

class ChallengeService {
  getTodayMissionVideos(roomId: number) {
    return fetchApi(CHALLENGE_API.GET_TODAY_VIDEOS(roomId));
  }

  uploadVideo(roomId: number, userId: number, youtubeUrl: string) {
    return fetchApi(CHALLENGE_API.UPLOAD_VIDEO(roomId), {
      method: "POST",
      body: JSON.stringify({ userId, youtubeUrl }),
    });
  }

  deleteVideo(videoId: number, userId: number) {
    return fetchApi(CHALLENGE_API.DELETE_VIDEO(videoId, userId), {
      method: "DELETE",
    });
  }
}

export const challengeService = new ChallengeService();