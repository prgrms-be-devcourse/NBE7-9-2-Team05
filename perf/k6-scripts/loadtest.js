import { generateJWT } from "./util.js";
import { getVideosToday, postCompleteMission, getTodayMissions } from "./scenarios/index.js";
import { sleep } from "k6";

export const options = {
  vus: 10,            // 동시 유저 수
  duration: "10s",    // 테스트 시간
};

const BASE_URL = "http://host.docker.internal:8080";
const ROOM_ID = 1;

export function setup() {
  const secret = __ENV.JWT_SECRET;
  const tokens = Array.from({ length: 10 }, (_, i) =>
    generateJWT({ id: i + 1, nickname: `PerfUser${i + 1}` }, secret)
  );
  return { tokens };
}

export default function (data) {
  const token = data.tokens[__VU - 1];
  const testId = __ENV.TEST_ID || "default-test";
  const r = Math.random();

  // 통합 시나리오
  if (r < 0.33) {
    getVideosToday(BASE_URL, token, testId);
  } else if (r < 0.66) {
    getTodayMissions(BASE_URL, token, testId, ROOM_ID);
  } else {
    postCompleteMission(BASE_URL, token, testId, ROOM_ID);
  }

  sleep(1);
}