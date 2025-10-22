"use client";

import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { challengeService } from "@/services";
import type { ChallengeVideo, ChallengeMissionStatus } from "@/types";
import { UploadVideoForm, VideoItem } from "@/components";

export default function RoomDetailPage() {
  const params = useParams();
  const router = useRouter();
  const roomId = Number(params.roomId);
  const [activeTab, setActiveTab] = useState<"feed" | "participants">("feed");
  const [videos, setVideos] = useState<ChallengeVideo[]>([]);
  const [loading, setLoading] = useState(true);
  const [missionStatus, setMissionStatus] = useState<string | null>(null);
  const [isCompleting, setIsCompleting] = useState(false);
  const [participants, setParticipants] = useState<ChallengeMissionStatus[]>([]);

  const handleLeaveRoom = async () => {
    if (!confirm("운동방에서 나가시겠습니까?")) return;

    try {
      await challengeService.leaveChallengeRoom(roomId);
      alert("운동방에서 탈퇴했습니다.");
      router.push("/rooms"); // 운동방 목록으로 이동
    } catch (err) {
      console.error("운동방 탈퇴 실패:", err);
      alert("운동방 탈퇴 중 오류가 발생했습니다.");
    }
  };

  const handleCompleteMission = async () => {
    if (isCompleting) return; // 이미 처리 중이면 무시
    setIsCompleting(true);

    try {
      const res = await challengeService.completeMission(roomId);

      // 백엔드가 성공적으로 응답하면
      setMissionStatus("오늘 운동이 완료되었습니다! 💪");
      console.log("미션 완료:", res);
      fetchParticipants();
    } catch (err: any) {
      console.error("미션 완료 실패:", err);

      // 백엔드에서 중복 완료 예외 메시지가 올 경우
      if (err instanceof Error && err.message.includes("이미 완료")) {
        setMissionStatus("오늘의 미션은 이미 완료되었습니다. ✅");
      } else {
        setMissionStatus("미션 완료 처리 중 오류가 발생했습니다.");
      }
    } finally {
      // 약간의 딜레이 후 버튼 재활성화 (UX적으로)
      setTimeout(() => setIsCompleting(false), 1000);
    }
  };

  // 영상 목록 조회
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
  // 참가자 현황 조회
  const fetchParticipants = async () => {
    try {
      const res = await challengeService.getTodayMissions(roomId);
      setParticipants(res.data || []);
    } catch (err) {
      console.error("참가자 현황 불러오기 실패:", err);
    }
  };

  useEffect(() => {
    fetchVideos();
    fetchParticipants();
  }, [roomId]);

  if (loading) return <p className="text-center mt-20">로딩 중...</p>;

  return (
    <div className="p-6 space-y-6">
      {/* ✅ 운동방 정보 카드 */}
      <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6">
        <h2 className="text-xl font-semibold text-gray-900">운동방 이름 (로딩 예정)</h2>
        <p className="text-gray-500 mt-2">
          운동방 설명이 여기에 표시됩니다. (API 연결 전까지 임시 문구)
        </p>
        <div className="mt-4 flex justify-between items-center">
          <span className="text-sm text-gray-400">참가자 수: -명</span>
          <button
            onClick={handleLeaveRoom}
            className="bg-red-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-red-700 transition"
          >
            운동방 나가기
          </button>
        </div>
      </div>
      {/* 탭 네비게이션 */}
      <div className="flex border-b mb-6">
        <button
          onClick={() => setActiveTab("feed")}
          className={`px-4 py-2 font-semibold transition-colors ${
            activeTab === "feed"
              ? "border-b-2 border-green-600 text-green-600"
              : "text-gray-500 hover:text-green-600"
          }`}
        >
          피드
        </button>
        <button
          onClick={() => setActiveTab("participants")}
          className={`ml-6 px-4 py-2 font-semibold transition-colors ${
            activeTab === "participants"
              ? "border-b-2 border-green-600 text-green-600"
              : "text-gray-500 hover:text-green-600"
          }`}
        >
          참가자
        </button>
      </div>

      {/* 피드 탭 */}
      {activeTab === "feed" && (
        <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4 border-b pb-3">
            오늘의 운동 영상
          </h2>

          {videos.length === 0 ? (
            <p className="text-gray-500 text-sm">
              아직 업로드된 영상이 없습니다.
            </p>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
              {videos.map((video) => (
                <VideoItem
                  key={video.id}
                  video={video}
                  onDeleted={fetchVideos}
                />
              ))}
            </div>
          )}

          {/* 새 영상 업로드 */}
          <div className="mt-8 border-t pt-6">
            <UploadVideoForm roomId={roomId} onUploadSuccess={fetchVideos} />
          </div>

          {/* 오늘 운동 완료 섹션 */}
          <div className="mt-8 border-t pt-6 flex items-center justify-between">
            <h3 className="text-base font-semibold text-gray-900">
              오늘 운동 완료
            </h3>
            <button
              onClick={handleCompleteMission}
              className="bg-green-600 text-white font-medium px-5 py-2 rounded-xl hover:bg-green-700 transition"
            >
              완료
            </button>
          </div>

          {/* 상태 메시지 */}
          {missionStatus && (
            <p
              className={`text-sm mt-3 ${
                missionStatus.includes("이미")
                  ? "text-yellow-600"
                  : missionStatus.includes("오류")
                  ? "text-red-600"
                  : "text-green-600"
              }`}
            >
              {missionStatus}
            </p>
          )}
        </div>
      )}

      {/* 참가자 탭 */}
      {activeTab === "participants" && (
        <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4 border-b pb-3">
            오늘의 참가자 현황
          </h2>

          {participants.length === 0 ? (
            <p className="text-gray-500 text-sm">
              참가자 데이터가 없습니다.
            </p>
          ) : (
            <ul className="space-y-3">
              {participants.map((p) => (
                <li
                  key={p.participantId}
                  className="flex items-center justify-between border border-gray-100 rounded-xl p-3 shadow-sm"
                >
                  <div className="flex items-center space-x-3">
                    <div className="w-8 h-8 bg-gray-200 rounded-full" />
                    <span className="font-medium text-gray-800">
                      참가자 {p.participantId}
                      {p.isHost === "HOST" && (
                        <span className="ml-2 text-xs text-blue-600 font-semibold">(방장)</span>
                      )}
                    </span>
                  </div>
                  <span
                    className={`text-sm px-3 py-1 rounded-full ${
                      p.completed
                        ? "bg-green-100 text-green-700"
                        : "bg-gray-100 text-gray-500"
                    }`}
                  >
                    {p.completed ? "완료" : "미완료"}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </div>
      )}
    </div>
  );
}