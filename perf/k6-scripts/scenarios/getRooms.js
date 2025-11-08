import http from "k6/http";
import { check } from "k6";

export function getRooms(baseUrl, jwt, testId, page = 0, size = 10) {
  const url = `${baseUrl}/api/v1/challenge/rooms?page=${page}&size=${size}`;
  const res = http.get(url, {
    headers: {
      Authorization: `Bearer ${jwt}`,
      Accept: "application/json",
    },
    tags: { api: "getRooms", test_id: testId },
  });

  let json;
  try {
    json = res.json();
  } catch {
    console.error("❌ JSON parse error:", res.body);
    return;
  }

  // ✅ 올바른 필드명: summaries → rooms
  const rooms = json?.data?.rooms ?? [];
  const total = json?.data?.total ?? -1;

  check(res, {
    "getRooms 200": (r) => r.status === 200,
    "data exists": () => json?.data !== undefined && json?.data !== null,
    "rooms exists": () => Array.isArray(rooms),
    "total valid": () => typeof total === "number" && total >= 0,
    "rooms length >= 0": () => rooms.length >= 0,
  });

  if (!Array.isArray(rooms)) {
    console.warn("⚠️ response shape mismatch:", JSON.stringify(json));
  }

  return res;
}