"use client";

import { useState } from "react";
import { challengeService } from "@/services";

interface UploadVideoFormProps {
  roomId: number;
  onUploadSuccess: () => void;
}

export default function UploadVideoForm({ roomId, onUploadSuccess }: UploadVideoFormProps) {
  const [youtubeUrl, setYoutubeUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{ type: "error" | "success"; text: string } | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!youtubeUrl.trim()) {
      setMessage({ type: "error", text: "유튜브 링크를 입력해주세요." });
      return;
    }

    setLoading(true);
    setMessage(null);

    try {
      await challengeService.uploadVideo(roomId, youtubeUrl.trim());
      setYoutubeUrl("");
      setMessage({ type: "success", text: "영상이 성공적으로 업로드되었습니다!" });
      onUploadSuccess();
    } catch (err) {
      console.error("영상 업로드 실패:", err);
      setMessage({
        type: "error",
        text: err instanceof Error ? err.message : "업로드 중 오류가 발생했습니다.",
      });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-white p-6 rounded-2xl shadow-md">
      <h2 className="text-xl font-semibold text-gray-900 mb-4">🎬 영상 업로드</h2>

      <form onSubmit={handleSubmit} className="flex flex-col sm:flex-row gap-3">
        <input
          type="text"
          value={youtubeUrl}
          onChange={(e) => setYoutubeUrl(e.target.value)}
          placeholder="유튜브 영상 URL을 입력하세요"
          className="flex-1 rounded-xl border border-gray-300 px-4 py-2 focus:ring-2 focus:ring-green-500 outline-none"
        />
        <button
          type="submit"
          disabled={loading}
          className="bg-green-600 text-white rounded-xl px-6 py-2 hover:bg-green-700 transition disabled:opacity-70 disabled:cursor-not-allowed"
        >
          {loading ? "업로드 중..." : "업로드"}
        </button>
      </form>

      {message && (
        <p
          className={`mt-3 text-sm ${
            message.type === "error" ? "text-red-500" : "text-green-600"
          }`}
        >
          {message.text}
        </p>
      )}
    </div>
  );
}