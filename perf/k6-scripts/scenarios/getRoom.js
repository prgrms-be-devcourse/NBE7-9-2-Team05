import http from "k6/http";
import { check } from "k6";

export function getRoom(baseUrl, jwt, testId, roomId = 1) {
  const url = `${baseUrl}/api/v1/challenge/rooms/${roomId}`;

  const res = http.get(url, {
    headers: {
      Authorization: `Bearer ${jwt}`,
    },
    tags: { api: "getRoom", test_id: testId },
  });

  check(res, {
    "getRoom 200": (r) => r.status === 200,
    "response has videos": (r) => r.body && r.body.includes("videos"),
    "response has participants": (r) => r.body && r.body.includes("participants"),
  });

  return res;
}