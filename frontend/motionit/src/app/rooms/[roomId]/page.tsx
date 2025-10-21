"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { challengeService } from "@/services";
import { ChallengeVideo } from "@/types/video";

export default function RoomPage() {
  const { roomId } = useParams();
  const [videos, setVideos] = useState<ChallengeVideo[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!roomId) return;

    const fetchVideos = async () => {
      try {
        setIsLoading(true);
        const res = await challengeService.getTodayMissionVideos(Number(roomId));

        // ✅ ResponseData 구조에 따라 다름
        // 백엔드가 ResponseData.success("메시지", data)로 감싸므로:
        setVideos(res.data ?? []);
      } catch (err) {
        console.error(err);
        setError("영상을 불러오는 중 오류가 발생했습니다.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchVideos();
  }, [roomId]);

  if (isLoading)
    return (
      <div className="flex min-h-screen items-center justify-center text-gray-500">
        로딩 중...
      </div>
    );

  if (error)
    return (
      <div className="flex min-h-screen items-center justify-center text-red-500">
        {error}
      </div>
    );

  return (
    <div className="min-h-screen bg-gray-50 px-4 py-8">
      <div className="mx-auto max-w-3xl space-y-8">
        <h1 className="text-3xl font-bold text-gray-900 text-center">
          오늘의 운동 영상 🎥
        </h1>

        {videos.length === 0 ? (
          <p className="text-center text-gray-500">
            오늘 업로드된 영상이 없습니다.
          </p>
        ) : (
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            {videos.map((video) => (
              <div
                key={video.id}
                className="bg-white rounded-2xl shadow-md overflow-hidden hover:shadow-lg transition"
              >
                <img
                  src={video.thumbnailUrl}
                  alt={video.title}
                  className="w-full h-48 object-cover"
                />
                <div className="p-4">
                  <h2 className="text-lg font-semibold text-gray-900 truncate">
                    {video.title}
                  </h2>
                  <p className="mt-1 text-sm text-gray-500">
                    업로드일: {new Date(video.uploadDate).toLocaleDateString()}
                  </p>
                  <a
                    href={`https://youtube.com/watch?v=${video.youtubeVideoId}`}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="mt-3 block text-center bg-green-600 text-white py-2 rounded-xl hover:bg-green-700 transition"
                  >
                    유튜브로 보기
                  </a>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}