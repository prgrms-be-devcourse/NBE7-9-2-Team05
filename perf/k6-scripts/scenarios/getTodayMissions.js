import http from "k6/http";
import { check } from "k6";

export function getTodayMissions(baseUrl, jwt, roomId, testId) {
  const res = http.get(
    `${baseUrl}/api/v1/challenge/rooms/${roomId}/missions/today`,
    {
      headers: { Authorization: `Bearer ${jwt}` },
      tags: { api: "getTodayMissions", test_id: testId },
    }
  );

  check(res, {
    "getTodayMissions 200": (r) => r.status === 200,
  });
}