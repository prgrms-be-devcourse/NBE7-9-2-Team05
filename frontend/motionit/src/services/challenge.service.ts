import { CHALLENGE_API } from "../constants";
import { fetchApi } from "./client";

class ChallengeService {
  // 오늘의 미션 영상 목록 조회
  getTodayMissionVideos(roomId: number) {
    return fetchApi(CHALLENGE_API.GET_TODAY_VIDEOS(roomId));
  }

  // 영상 업로드
  uploadVideo(roomId: number, youtubeUrl: string) {
    return fetchApi(CHALLENGE_API.UPLOAD_VIDEO(roomId), {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ youtubeUrl }),
    });
  }

  // 영상 삭제
  deleteVideo(videoId: number) {
    return fetchApi(CHALLENGE_API.DELETE_VIDEO(videoId), {
      method: "DELETE",
    });
  }

  // 미션 완료 처리
  completeMission(roomId: number) {
    return fetchApi(CHALLENGE_API.COMPLETE_MISSION(roomId), {
      method: "POST",
    });
  }
  
  // 오늘의 참가자 미션 상태 조회
  getTodayMissions(roomId: number) {
    return fetchApi(CHALLENGE_API.GET_TODAY_MISSIONS(roomId));
  }
}

export const challengeService = new ChallengeService();