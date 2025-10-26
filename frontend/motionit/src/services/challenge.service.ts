import { CHALLENGE_API } from "../constants";
import { CreateRoomRequest } from "../type";
import api from "./client";

class ChallengeService {
  // 오늘의 미션 영상 목록 조회
  getTodayMissionVideos(roomId: number) {
    return api.get(CHALLENGE_API.GET_TODAY_VIDEOS(roomId));
  }

  // 영상 업로드
  uploadVideo(roomId: number, youtubeUrl: string) {
    return api.post(CHALLENGE_API.UPLOAD_VIDEO(roomId), { youtubeUrl });
  }

  // 영상 삭제
  deleteVideo(roomId: number, videoId: number) {
    return api.delete(CHALLENGE_API.DELETE_VIDEO(roomId, videoId));
  }

  // 미션 완료 처리
  completeMission(roomId: number) {
    return api.post(CHALLENGE_API.COMPLETE_MISSION(roomId));
  }

  // ai 응원 메세지 생성
  getAiSummary(roomId: number) {
    return api.get(CHALLENGE_API.GET_AI_SUMMARY(roomId));
  }

  // 오늘의 참가자 미션 상태 조회
  getTodayMissions(roomId: number) {
    return api.get(CHALLENGE_API.GET_TODAY_MISSIONS(roomId));
  }

  // 운동방 탈퇴
  leaveChallengeRoom(roomId: number) {
    return api.post(CHALLENGE_API.LEAVE_ROOM(roomId));
  }

  // 운동방 참가자 여부 조회
  getParticipationStatus(roomId: number) {
    return api.get(CHALLENGE_API.GET_PARTICIPATION_STATUS(roomId));
  }

  // ✅ 댓글 목록 조회
  getComments(roomId: number, page = 0, size = 10) {
    return api.get(CHALLENGE_API.GET_COMMENTS(roomId, page, size));
  }

  // ✅ 댓글 작성
  createComment(roomId: number, content: string) {
    return api.post(CHALLENGE_API.CREATE_COMMENT(roomId), { content });
  }

  // ✅ 댓글 수정
  editComment(roomId: number, commentId: number, content: string) {
    return api.patch(CHALLENGE_API.EDIT_COMMENT(roomId, commentId), { content });
  }

  // ✅ 댓글 삭제
  deleteComment(roomId: number, commentId: number) {
    return api.delete(CHALLENGE_API.DELETE_COMMENT(roomId, commentId));
  }

  // ✅ 댓글 좋아요 토글
  toggleCommentLike(commentId: number) {
    return api.post(CHALLENGE_API.TOGGLE_COMMENT_LIKE(commentId));
  }

  createRoom(payload: CreateRoomRequest) {
    return api.post(CHALLENGE_API.GET_OR_CREATE_ROOMS(), payload);
  }

  getRoomDetail(roomId: number){
    return api.get(CHALLENGE_API.GET_ONLY_ROOM_DETAIL(roomId));
  }

  getRooms(page: number, size: number) {
    const params = new URLSearchParams({
      page: String(page),
      size: String(size),
    });

    return api.get(`${CHALLENGE_API.GET_OR_CREATE_ROOMS()}?${params.toString()}`);
  }

  deleteRoom(roomId: number) {
    return api.delete(CHALLENGE_API.GET_OR_DELETE_ROOM(roomId));
  }

  joinRoom(roomId: number) {
    return api.post(CHALLENGE_API.JOIN_ROOM(roomId));
  }
}

export const challengeService = new ChallengeService();