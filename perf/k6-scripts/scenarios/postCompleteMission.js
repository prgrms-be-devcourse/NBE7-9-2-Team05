import http from "k6/http";
import { check } from "k6";

export function postCompleteMission(baseUrl, token, testId, roomId) {
  const res = http.post(
    `${baseUrl}/api/v1/challenge/rooms/${roomId}/missions/complete`,
    null, // POST body 없음
    {
      headers: { Authorization: `Bearer ${token}` },
      tags: { api: "postCompleteMission", test_id: testId },
    }
  );

  check(res, {
    "postCompleteMission 200": (r) => r.status === 200,
  });
}