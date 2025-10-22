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
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!youtubeUrl.trim()) {
      setError("유튜브 링크를 입력해주세요.");
      return;
    }

    setLoading(true);
    setError(null);
    setSuccess(null);

    try {
      await challengeService.uploadVideo(roomId, youtubeUrl);
      // userId는 백엔드에서 requestContext로 인증되므로 의미없는 값(0) 전달
      setYoutubeUrl("");
      setSuccess("영상이 성공적으로 업로드되었습니다!");
      onUploadSuccess(); // 목록 새로고침
    } catch (err) {
      console.error(err);
      setError(err instanceof Error ? err.message : "업로드 중 오류가 발생했습니다.");
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

      {error && <p className="text-red-500 text-sm mt-3">{error}</p>}
      {success && <p className="text-green-600 text-sm mt-3">{success}</p>}
    </div>
  );
}