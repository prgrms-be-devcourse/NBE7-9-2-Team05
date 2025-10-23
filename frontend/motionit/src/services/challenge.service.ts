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
  deleteVideo(roomId:number, videoId: number) {
    return fetchApi(CHALLENGE_API.DELETE_VIDEO(roomId, videoId), {
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

  // 운동방 탈퇴
  leaveChallengeRoom(roomId: number) {
    return fetchApi(CHALLENGE_API.LEAVE_ROOM(roomId), {
      method: "POST",
    });
  }

  // 운동방 참가자 여부 조회
  getParticipationStatus(roomId: number) {
    return fetchApi(CHALLENGE_API.GET_PARTICIPATION_STATUS(roomId));
  }

  // ✅ 댓글 목록 조회 (페이지네이션)
  getComments(roomId: number, page = 0, size = 20) {
    return fetchApi(CHALLENGE_API.GET_COMMENTS(roomId, page, size));
  }

  // ✅ 댓글 작성
  createComment(roomId: number, content: string) {
    return fetchApi(CHALLENGE_API.CREATE_COMMENT(roomId), {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ content }),
    });
  }

  // ✅ 댓글 수정
  editComment(roomId: number, commentId: number, content: string) {
    return fetchApi(CHALLENGE_API.EDIT_COMMENT(roomId, commentId), {
      method: "PATCH",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ content }),
    });
  }

  // ✅ 댓글 삭제
  deleteComment(roomId: number, commentId: number) {
    return fetchApi(CHALLENGE_API.DELETE_COMMENT(roomId, commentId), {
      method: "DELETE",
    });
  }
}

export const challengeService = new ChallengeService();