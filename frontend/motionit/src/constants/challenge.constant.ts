export const CHALLENGE_API = {
  GET_TODAY_VIDEOS: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/videos/today`,
  UPLOAD_VIDEO: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/videos`,
  DELETE_VIDEO: (videoId: number) => `/api/v1/challenge/videos/${videoId}`,
  COMPLETE_MISSION: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/missions/complete`,
  GET_TODAY_MISSIONS: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/missions/today`,
  LEAVE_ROOM: (roomId: number) => `/api/v1/challenge/participants/${roomId}/leave`,
  GET_PARTICIPATION_STATUS: (roomId: number) => `/api/v1/challenge/participants/${roomId}/status`,
};