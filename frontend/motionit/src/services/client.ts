import axios from "axios";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  withCredentials: true, // 쿠키 자동 포함
  headers: { "Content-Type": "application/json" },
});

// ✅ refresh 전용 클라이언트 추가
const refreshClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  withCredentials: true,
});

let isRefreshing = false;
let refreshQueue: (() => void)[] = [];

// ✅ Access Token 자동 재발급 로직
async function handleTokenRefresh() {
  if (isRefreshing) {
    return new Promise<void>((resolve) => refreshQueue.push(resolve));
  }

  isRefreshing = true;
  try {
    console.log("🔄 Access Token 재발급 요청...");
    const res = await refreshClient.post("/api/v1/auth/refresh");

    if (res.data?.resultCode === "SUCCESS") {
      console.log("✅ Access Token 재발급 성공");
    }

    refreshQueue.forEach((resolve) => resolve());
    refreshQueue = [];
  } catch (err) {
    console.error("❌ Access Token 재발급 실패:", err);
    refreshQueue = [];
    throw err;
  } finally {
    isRefreshing = false;
  }
}

// ✅ 요청 인터셉터 (로그용)
api.interceptors.request.use((config) => {
  console.log("📡 요청:", config.method?.toUpperCase(), config.url, config.data || "");
  return config;
});

// ✅ 응답 인터셉터 (기존 구조 그대로 + 재발급 기능 추가)
api.interceptors.response.use(
  (response) => {
    console.log("✅ 응답 성공:", response.data);
    return response.data; // 백엔드가 { resultCode, msg, data } 구조 반환
  },
  async (error) => {
    const { response, config } = error;

    if (!response) {
      console.error("🔥 서버 연결 실패:", error);
      return Promise.reject(new Error("서버에 연결할 수 없습니다."));
    }

    const { status, data } = response;
    const code = data?.resultCode;
    const message = data?.msg || "요청 중 오류가 발생했습니다.";

    console.error("❌ 응답 실패:", status, code, message);

    // ✅ Access Token 만료 (U-108) → refresh 요청
    if (status === 401 && code === "U-108" && !config._retry) {
      config._retry = true;
      try {
        await handleTokenRefresh();
        console.log("🔁 토큰 재발급 후 원래 요청 재시도:", config.url);
        return api(config);
      } catch (refreshError) {
        console.error("🚫 재발급 실패 → 로그인 페이지 이동");
        if (typeof window !== "undefined") {
          alert("세션이 만료되었습니다. 다시 로그인해주세요.");
          window.location.href = "/auth/login";
        }
        return Promise.reject(refreshError);
      }
    }

    // ✅ Refresh Token 관련 에러 → 로그인 이동
    const refreshErrors = ["U-109", "U-110", "U-111", "U-112", "U-113"];
    if (status === 401 && refreshErrors.includes(code)) {
      console.warn("🚫 Refresh Token 오류 → 로그인 이동");
      if (typeof window !== "undefined") {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        window.location.href = "/auth/login";
      }
      return Promise.reject(new Error(message));
    }

    // ✅ 기타 에러 - 사용자 알림
    if (typeof window !== "undefined" && !config._retry) {
      alert(message);
    }

    return Promise.reject(new Error(message));
  }
);

export default api;

// ✅ fetchApi (기존 구조 유지 + 재발급 기능만 추가)
export const fetchApi = async (url: string, options?: RequestInit) => {
  const fullUrl = `${process.env.NEXT_PUBLIC_API_BASE_URL}${url}`;

  const makeRequest = async () => {
    const finalOptions: RequestInit = {
      ...options,
      credentials: "include", // 쿠키 자동 포함
    };

    if (finalOptions.body && !finalOptions.headers) {
      finalOptions.headers = { "Content-Type": "application/json" };
    }

    return fetch(fullUrl, finalOptions);
  };

  try {
    console.log("📡 Fetch 요청:", fullUrl);
    let res = await makeRequest();

    // ✅ Access Token 만료 시 재발급 처리
    if (res.status === 401) {
      const resultData = await res.json().catch(() => ({}));
      const code = resultData.resultCode;
      console.log("⚠️ 401 에러 감지:", code, resultData.msg);

      if (code === "U-108") {
        console.log("🔄 Access Token 만료 → 재발급 후 재시도");
        try {
          await handleTokenRefresh();
          res = await makeRequest();
          if (!res.ok) {
            const retryData = await res.json().catch(() => ({}));
            console.error("❌ 재시도 후에도 실패:", res.status, retryData);
            throw new Error(retryData.msg || "재시도 후에도 요청 실패");
          }
        } catch (refreshErr) {
          console.error("🚫 토큰 재발급 실패:", refreshErr);
          if (typeof window !== "undefined") {
            alert("세션이 만료되었습니다. 다시 로그인해주세요.");
            window.location.href = "/auth/login";
          }
          throw refreshErr;
        }
      }
    }

    // ✅ 일반 에러 처리 (401 이외)
    if (!res.ok) {
      const resultData = await res.json().catch(() => ({}));
      console.error("❌ Fetch 실패:", res.status, resultData);
      throw new Error(resultData.msg || `HTTP ${res.status}`);
    }

    // ✅ 성공 응답
    const data = await res.json();
    console.log("✅ Fetch 성공:", data);
    return data;
  } catch (err) {
    console.error("🔥 Fetch 예외:", err);
    throw err;
  }
};
