export const CHALLENGE_API = {
  GET_TODAY_VIDEOS: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/videos/today`,
  UPLOAD_VIDEO: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/videos`,
  DELETE_VIDEO: (roomId: number, videoId: number) => `/api/v1/challenge/rooms/${roomId}/videos/${videoId}`,
  COMPLETE_MISSION: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/missions/complete`,
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
};