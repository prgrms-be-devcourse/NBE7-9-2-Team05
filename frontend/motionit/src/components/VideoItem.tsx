"use client";

import type { ChallengeVideo } from "@/type";
import { challengeService } from "@/services";

interface VideoItemProps {
  video: ChallengeVideo;
  roomId: number;
  onDeleted: () => void;
}

export default function VideoItem({ video, roomId, onDeleted }: VideoItemProps) {
  const handleDeleteVideo = async () => {
    if (!confirm("정말로 이 영상을 삭제하시겠습니까?")) return;

    try {
      await challengeService.deleteVideo(roomId, video.id);
      alert("영상이 삭제되었습니다.");
      onDeleted();
    } catch (err) {
      console.error("영상 삭제 실패:", err);
      // 에러 alert은 인터셉터에서 이미 처리됨
    }
  };

  return (
    <div className="group bg-white rounded-2xl shadow-md overflow-hidden hover:shadow-lg transition relative">
      {/* ✅ 썸네일 - 유튜브 비율 16:9, 여백 완전 제거 */}
      <div className="relative w-full aspect-[16/9] overflow-hidden bg-gray-100">
        <img
          src={video.thumbnailUrl}
          alt={video.title}
          loading="lazy"
          className="w-full h-full object-cover transition-transform duration-300 group-hover:scale-105"
        />
      </div>

      {/* ✅ 정보 영역 */}
      <div className="p-5">
        <h2 className="text-lg sm:text-xl font-semibold text-gray-900 truncate">
          {video.title}
        </h2>
        <p className="mt-1 text-sm text-gray-500">업로드일: {video.uploadDate}</p>

        {/* ✅ 버튼 영역 */}
        <div className="flex justify-between items-center mt-4">
          <a
            href={`https://youtube.com/watch?v=${video.youtubeVideoId}`}
            target="_blank"
            rel="noopener noreferrer"
            className="flex-1 text-center bg-green-600 text-white py-2.5 rounded-xl hover:bg-green-700 transition mr-2"
          >
            유튜브로 보기
          </a>
          <button
            onClick={handleDeleteVideo}
            className="bg-red-500 text-white px-3 py-2.5 rounded-xl hover:bg-red-600 transition"
          >
            삭제
          </button>
        </div>
      </div>
    </div>
  );
}