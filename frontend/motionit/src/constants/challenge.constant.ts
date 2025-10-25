import { ChallengeStatus } from "../type";

export const CHALLENGE_API = {
  GET_TODAY_VIDEOS: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/videos/today`,
  UPLOAD_VIDEO: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/videos`,
  DELETE_VIDEO: (roomId: number, videoId: number) => `/api/v1/challenge/rooms/${roomId}/videos/${videoId}`,
  COMPLETE_MISSION: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/missions/complete`,
  GET_AI_SUMMARY: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/missions/ai-summary`,
  GET_TODAY_MISSIONS: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/missions/today`,
  LEAVE_ROOM: (roomId: number) => `/api/v1/challenge/participants/${roomId}/leave`,
  GET_PARTICIPATION_STATUS: (roomId: number) => `/api/v1/challenge/participants/${roomId}/status`,
  GET_COMMENTS: (roomId: number, page = 0, size = 10) =>
    `/api/v1/rooms/${roomId}/comments?page=${page}&size=${size}`,
  CREATE_COMMENT: (roomId: number) => `/api/v1/rooms/${roomId}/comments`,
  EDIT_COMMENT: (roomId: number, commentId: number) =>
    `/api/v1/rooms/${roomId}/comments/${commentId}`,
  DELETE_COMMENT: (roomId: number, commentId: number) =>
    `/api/v1/rooms/${roomId}/comments/${commentId}`,
  TOGGLE_COMMENT_LIKE: (commentId: number) => `/api/v1/comments/${commentId}/likes`,
  GET_ONLY_ROOM_DETAIL: (roomId: number) => `/api/v1/challenge/rooms/${roomId}`,
  GET_OR_CREATE_ROOMS: () => `/api/v1/challenge/rooms`,
  GET_OR_DELETE_ROOM: (roomId: number) => `/api/v1/challenge/rooms/${roomId}`,
  JOIN_ROOM: (roomId: number) => `/api/v1/challenge/participants/${roomId}/join`,
};

export const ROOM_TAB = {
  [ChallengeStatus.ALL]: '전체',
  [ChallengeStatus.JOINABLE]: '참여가능',
  [ChallengeStatus.JOINING]: '참여중',
}
