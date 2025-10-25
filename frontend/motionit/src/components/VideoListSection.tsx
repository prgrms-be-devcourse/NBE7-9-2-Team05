"use client";

import { useState } from "react";
import type { ChallengeVideo } from "@/type";
import { VideoItem } from "@/components";
import Pagination from "@/components/common/Pagination";

interface VideoListSectionProps {
  videos: ChallengeVideo[];
  roomId: number;
  onRefresh: () => void;
}

export default function VideoListSection({ videos, roomId, onRefresh }: VideoListSectionProps) {
  const pageSize = 4; // 한 페이지당 4개 영상
  const totalPages = Math.ceil(videos.length / pageSize);
  const [currentPage, setCurrentPage] = useState(0);

  // 현재 페이지에 맞는 영상만 슬라이싱
  const startIdx = currentPage * pageSize;
  const currentVideos = videos.slice(startIdx, startIdx + pageSize);

  return (
    <div className="space-y-6">
      {/* 영상 목록 */}
      <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 transition-all duration-300">
        {currentVideos.map((video) => (
          <VideoItem key={video.id} video={video} roomId={roomId} onDeleted={onRefresh} />
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