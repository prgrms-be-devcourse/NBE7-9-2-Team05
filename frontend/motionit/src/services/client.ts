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
