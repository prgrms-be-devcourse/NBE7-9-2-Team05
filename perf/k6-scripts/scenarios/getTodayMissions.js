import http from "k6/http";
import { check } from "k6";

export function getTodayMissions(baseUrl, token, testId, roomId) {
  const res = http.get(
    `${baseUrl}/api/v1/challenge/rooms/${roomId}/missions/today`,
    {
      headers: { Authorization: `Bearer ${token}` },
      tags: { api: "getTodayMissions", test_id: testId },
    }
  );

  check(res, {
    "getTodayMissions 200": (r) => r.status === 200,
  });
}