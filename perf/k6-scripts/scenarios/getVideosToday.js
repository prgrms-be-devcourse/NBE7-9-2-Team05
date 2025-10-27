import http from "k6/http";
import { check } from "k6";

export function getVideosToday(baseUrl, token, testId) {
  const res = http.get(
    `${baseUrl}/api/v1/challenge/rooms/1/videos/today`,
    {
      headers: { Authorization: `Bearer ${token}` },
      tags: { api: "getVideosToday", test_id: testId },
    }
  );

  check(res, {
    "getVideosToday 200": (r) => r.status === 200,
  });
}