"use client";

import { useParams } from "next/navigation";
import { useEffect, useState } from "react";
import { challengeService } from "@/services";
import type { ChallengeVideo } from "@/types";
import { UploadVideoForm, VideoItem } from "@/components";

export default function RoomDetailPage() {
  const params = useParams();
  const roomId = Number(params.roomId);
  const [videos, setVideos] = useState<ChallengeVideo[]>([]);
  const [loading, setLoading] = useState(true);

  const fetchVideos = async () => {
    try {
      const data = await challengeService.getTodayMissionVideos(roomId);
      setVideos(data.data || []);
    } catch (err) {
      console.error("영상 불러오기 실패:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchVideos();
  }, [roomId]);

  if (loading) return <p className="text-center mt-20">로딩 중...</p>;

  return (
    <div className="p-6">
      <UploadVideoForm roomId={roomId} onUploadSuccess={fetchVideos} />

      <div className="mt-8 grid grid-cols-1 sm:grid-cols-2 gap-6">
        {videos.length === 0 ? (
          <p className="text-gray-500">아직 업로드된 영상이 없습니다.</p>
        ) : (
          videos.map((video) => (
            <VideoItem key={video.id} video={video} onDeleted={fetchVideos} />
          ))
        )}
      </div>
    </div>
  );
}