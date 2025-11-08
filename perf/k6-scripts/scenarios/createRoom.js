import http from "k6/http";
import { check } from "k6";

export function createRoom(baseUrl, jwt, testId) {
  const payload = JSON.stringify({
    title: `부하테스트방-${__VU}-${__ITER}`,
    description: "자동 생성된 테스트용 방",
    capacity: 50, // Integer
    duration: 7,  // Integer (일수 혹은 주 단위로 서버에서 정의한 값)
    videoUrl: "https://www.youtube.com/watch?v=2fpek3wzSZo",
    imageFileName: "perf_room_test.jpg",
    contentType: "image/jpeg"
  });

  const res = http.post(`${baseUrl}/api/v1/challenge/rooms`, payload, {
    headers: {
      Authorization: `Bearer ${jwt}`,
      "Content-Type": "application/json",
    },
    tags: { api: "createRoom", test_id: testId },
  });

  check(res, {
    "createRoom 200": (r) => r.status === 200,
  });
}