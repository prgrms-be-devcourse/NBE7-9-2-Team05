import axios from "axios";

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE_URL,
  withCredentials: true, // 쿠키 자동 포함
  headers: { "Content-Type": "application/json" },
});

// 요청 인터셉터 (로그용)
api.interceptors.request.use((config) => {
  console.log("📡 요청:", config.method?.toUpperCase(), config.url, config.data || "");
  return config;
});

// 응답 인터셉터
api.interceptors.response.use(
  (response) => {
    console.log("✅ 응답 성공:", response.data);
    return response.data; // 백엔드가 { resultCode, msg, data } 구조 반환
  },
  (error) => {
    const { response } = error;
    if (response) {
      console.error("❌ 응답 실패:", response.status, response.data);

      const message =
        response.data?.msg || "요청 중 오류가 발생했습니다.";

      // 사용자 알림
      if (typeof window !== "undefined") {
        alert(message);
      }

      return Promise.reject(new Error(message));
    } else {
      console.error("🔥 서버 연결 실패:", error);
      return Promise.reject(new Error("서버에 연결할 수 없습니다."));
    }
  }
);

export default api;

export const fetchApi = async (url: string, options?: RequestInit) => {
    try {
      const finalOptions: RequestInit = {
        ...options,
        credentials: "include", // 쿠키 자동 포함
      };

      if (finalOptions.body && !finalOptions.headers) {
        finalOptions.headers = { "Content-Type": "application/json" };
      }

      const fullUrl = `${process.env.NEXT_PUBLIC_API_BASE_URL}${url}`;
      console.log("📡 Fetch 요청:", fullUrl, finalOptions);

      const res = await fetch(fullUrl, finalOptions);

      if (!res.ok) {
        const resultData = await res.json().catch(() => ({}));
        console.error("❌ Fetch 실패:", res.status, resultData);
        throw new Error(resultData.msg || `HTTP ${res.status}`);
      }

      const data = await res.json();
      console.log("✅ Fetch 성공:", data);
      return data;
    } catch (err) {
      console.error("🔥 Fetch 예외:", err);
      throw err;
    }
  };
