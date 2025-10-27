import encoding from "k6/encoding";
import crypto from "k6/crypto";


function sign(data, secretBase64) {
  // Base64 decode 후 HMAC 키로 사용 (서버와 동일)
  const secretBytes = encoding.b64decode(secretBase64, "std"); 
  const hasher = crypto.createHMAC("sha256", secretBytes);
  hasher.update(data);
  return hasher.digest("base64")
    .replace(/\//g, "_")
    .replace(/\+/g, "-")
    .replace(/=/g, "");
}


export function generateJWT(payload, secretBase64) {
  const header = { alg: "HS256", typ: "JWT" };

  const issuedAt = Math.floor(Date.now() / 1000);
  const exp = issuedAt + 3600; // 1시간짜리 토큰

  const fullPayload = { ...payload, iat: issuedAt, exp };

  const encodedHeader = encoding.b64encode(JSON.stringify(header), "rawurl");
  const encodedPayload = encoding.b64encode(JSON.stringify(fullPayload), "rawurl");
  const signature = sign(`${encodedHeader}.${encodedPayload}`, secretBase64);
  const token = `${encodedHeader}.${encodedPayload}.${signature}`;

  console.log("✅ Generated JWT:", token);
  return token;
}