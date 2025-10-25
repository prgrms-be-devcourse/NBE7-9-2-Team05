"use client";

import { useEffect, useMemo, useState } from "react";
import type { ChallengeVideo } from "@/type";
import { VideoItem } from "@/components";
import Pagination from "@/components/common/Pagination";

interface VideoListSectionProps {
  videos: ChallengeVideo[];
  roomId: number;
  onRefresh: () => void;
}

export default function VideoListSection({
  videos,
  roomId,
  onRefresh,
}: VideoListSectionProps) {
  const pageSize = 4; // 한 페이지당 4개
  const [currentPage, setCurrentPage] = useState(0);

  // 총 페이지 수
  const totalPages = useMemo(
    () => Math.max(1, Math.ceil((videos?.length ?? 0) / pageSize)),
    [videos?.length]
  );

  // 현재 페이지에 맞는 영상
  const currentVideos = useMemo(() => {
    const startIdx = currentPage * pageSize;
    return (videos ?? []).slice(startIdx, startIdx + pageSize);
  }, [videos, currentPage]);

  // 비디오 개수 변동 시 현재 페이지 보정
  useEffect(() => {
    if (currentPage > totalPages - 1) {
      setCurrentPage(totalPages - 1);
    }
  }, [totalPages, currentPage]);

  // 비어있는 상태 처리
  if (!videos || videos.length === 0) {
    return (
      <div className="flex items-center justify-center rounded-xl border border-dashed border-gray-200 bg-white p-10 text-gray-500">
        아직 업로드된 영상이 없습니다.
      </div>
    );
  }

  return (
    <div className="space-y-6">
      {/* 영상 목록 (2×2 반응형, 4:3 비율) */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 transition-all duration-300">
        {currentVideos.map((video) => (
          <VideoItem
            key={video.id}
            video={video}
            roomId={roomId}
            onDeleted={onRefresh}
          />
        ))}
      </div>

      {/* 페이지 네비게이션 */}
      {totalPages > 1 && (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          onPageChange={setCurrentPage}
          groupSize={5}
        />
      )}
    </div>
  );
}