import { createRoom } from "../scenarios/createRoom.js";
import { CONFIG } from "../config.js";
import { generateJWT } from "../util/jwt.js";

export const options = { vus: CONFIG.VUS, duration: CONFIG.DURATION };

export function setup() {
  const secret = CONFIG.JWT_SECRET;
  const tokens = Array.from({ length: CONFIG.VUS }, (_, i) =>
    generateJWT({ id: i + 1, nickname: `PerfUser${i + 1}` }, secret)
  );
  const testId = new Date().toISOString().replace(/[:.]/g, "-");
  return { tokens, testId };
}

export default function (data) {
  const jwt = data.tokens[__VU - 1];
  createRoom(CONFIG.BASE_URL, jwt, data.testId);
}