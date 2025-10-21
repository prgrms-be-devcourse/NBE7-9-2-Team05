export const CHALLENGE_API = {
    GET_TODAY_VIDEOS: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/videos/today`,
    UPLOAD_VIDEO: (roomId: number) => `/api/v1/challenge/rooms/${roomId}/videos`,
    DELETE_VIDEO: (videoId: number, userId: number) => `/api/v1/challenge/videos/${videoId}?userId=${userId}`,
  };