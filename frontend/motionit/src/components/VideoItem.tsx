"use client";

import type { ChallengeVideo } from "@/type";
import { challengeService } from "@/services";

interface VideoItemProps {
  video: ChallengeVideo;
  onDeleted: () => void;
}

export default function VideoItem({ video, onDeleted }: VideoItemProps) {
  const handleDeleteVideo = async () => {
    if (!confirm("정말로 이 영상을 삭제하시겠습니까?")) return;

    try {
      await challengeService.deleteVideo(video.id);
      alert("영상이 삭제되었습니다.");
      onDeleted();
    } catch (err) {
      console.error("영상 삭제 실패:", err);
      alert("삭제 중 오류가 발생했습니다.");
    }
  };

  return (
    <div className="bg-white rounded-2xl shadow-md overflow-hidden hover:shadow-lg transition relative">
      <img
        src={video.thumbnailUrl}
        alt={video.title}
        className="w-full h-48 object-cover"
      />
      <div className="p-4">
        <h2 className="text-lg font-semibold text-gray-900 truncate">
          {video.title}
        </h2>
        <p className="mt-1 text-sm text-gray-500">업로드일: {video.uploadDate}</p>

        <div className="flex justify-between items-center mt-3">
          <a
            href={`https://youtube.com/watch?v=${video.youtubeVideoId}`}
            target="_blank"
            rel="noopener noreferrer"
            className="flex-1 text-center bg-green-600 text-white py-2 rounded-xl hover:bg-green-700 transition mr-2"
          >
            유튜브로 보기
          </a>

          <button
            onClick={handleDeleteVideo}
            className="bg-red-500 text-white px-3 py-2 rounded-xl hover:bg-red-600 transition"
          >
            삭제
          </button>
        </div>
      </div>
    </div>
  );
}