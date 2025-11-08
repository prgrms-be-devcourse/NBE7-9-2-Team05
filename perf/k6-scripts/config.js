export const CONFIG = {
  VUS: parseInt(__ENV.VUS || '10', 10),
  DURATION: __ENV.DURATION || '10s',
  ROOM_ID: parseInt(__ENV.ROOM_ID || '1', 10),
  BASE_URL: __ENV.BASE_URL || 'http://host.docker.internal:8080',
  JWT_SECRET: __ENV.JWT_SECRET,
  ENABLE_LOG: (__ENV.ENABLE_LOG || 'false') === 'true',
};