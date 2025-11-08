import http from "k6/http";
import { check } from "k6";

export function getVideosToday(baseUrl, jwt, roomId, testId) {
  const res = http.get(
    `${baseUrl}/api/v1/challenge/rooms/${roomId}/videos/today`,
    {
      headers: { Authorization: `Bearer ${jwt}` },
      tags: { api: "getVideosToday", test_id: testId },
    }
  );

  check(res, {
    "getVideosToday 200": (r) => r.status === 200,
  });
}