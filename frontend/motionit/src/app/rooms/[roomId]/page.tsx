/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { challengeService } from "@/services";
import type { ChallengeVideo, ChallengeMissionStatus } from "@/type";
import { UploadVideoForm, VideoItem, CommentSection, VideoListSection } from "@/components";
import ParticipantListSection from "@/components/ParticipantListSection";

export default function RoomDetailPage() {
  const params = useParams();
  const router = useRouter();
  const roomId = Number(params.roomId);

  const [activeTab, setActiveTab] = useState<"feed" | "participants">("feed");
  const [videos, setVideos] = useState<ChallengeVideo[]>([]);
  const [loading, setLoading] = useState(true);
  const [missionStatus, setMissionStatus] = useState<string | null>(null);
  const [aiSummary, setAiSummary] = useState<string | null>(null);
  const [isCompleting, setIsCompleting] = useState(false);
  const [participants, setParticipants] = useState<ChallengeMissionStatus[]>([]);
  const [isAuthorized, setIsAuthorized] = useState<boolean | null>(null);

  // 페이지 접근 검증
  const checkParticipationStatus = async () => {
    try {
      const res = await challengeService.getParticipationStatus(roomId);
      const joined = res.data?.joined ?? false;

      if (!joined) {
        alert("이 운동방에 참여하지 않았습니다. 목록으로 이동합니다.");
        router.push("/rooms");
      } else {
        setIsAuthorized(true);
      }
    } catch (err) {
      console.error("참여 여부 확인 실패:", err);
      alert("운동방 참여 여부 확인 중 오류가 발생했습니다.");
      router.push("/rooms");
    }
  };

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
    if (isCompleting) return; // 중복 클릭 방지
    setIsCompleting(true);
    setAiSummary(null); // 이전 응원문구 초기화
  
    try {
      // 즉시 미션 완료
      const res = await challengeService.completeMission(roomId);
      setMissionStatus("오늘 운동이 완료되었습니다! 💪");
      console.log("미션 완료 응답:", res);
  
      // 완료 후 AI 응원 메시지 비동기 호출
      try {
        const aiRes = await challengeService.getAiSummary(roomId);
        const message = aiRes?.data ?? null;
        setAiSummary(message || "응원 메시지를 불러오지 못했습니다 😢");
        console.log("AI 응원 메시지:", message);
      } catch (e) {
        console.error("AI 응원 메시지 요청 실패:", e);
        setAiSummary("응원 메시지를 불러오지 못했습니다 😢");
      }
  
      fetchParticipants(); // 상태 최신화
    } catch (err: any) {
      console.error("미션 완료 실패:", err);
      if (err instanceof Error && err.message.includes("이미 완료")) {
        setMissionStatus("오늘의 미션은 이미 완료되었습니다. ✅");
      } else {
        setMissionStatus("미션 완료 처리 중 오류가 발생했습니다.");
      }
    } finally {
      setIsCompleting(false);
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

  // 페이지 최초 진입 시 참여자 검증 → 이후 데이터 로드
  useEffect(() => {
    if (!roomId) return;
    checkParticipationStatus().then(() => {
      fetchVideos();
      fetchParticipants();
    });
  }, [roomId]);

  // 참여자 여부 로딩 중이거나 미참여자일 때 렌더링 차단
  if (isAuthorized === null || loading) {
    return <p className="text-center mt-20">로딩 중...</p>;
  }

  if (!isAuthorized) {
    return <p className="text-center mt-20 text-red-500">접근 권한이 없습니다.</p>;
  }

  return (
    <div className="p-6 space-y-6">
      {/* 운동방 정보 카드 */}
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

          {/* 운동 영상 */}
          {videos.length === 0 ? (
            <p className="text-gray-500 text-sm">아직 업로드된 영상이 없습니다.</p>
          ) : (
            <VideoListSection videos={videos} roomId={roomId} onRefresh={fetchVideos} />
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

          {/* 상태 메시지 + ✅ AI 응원문구 */}
          {missionStatus && (
            <div className="mt-3 space-y-2">
              <p
                className={`text-sm ${
                  missionStatus.includes("이미")
                    ? "text-yellow-600"
                    : missionStatus.includes("오류")
                    ? "text-red-600"
                    : "text-green-600"
                }`}
              >
                {missionStatus}
              </p>
              {aiSummary && (
                <p className="text-sm text-gray-700 bg-green-50 border border-green-100 rounded-xl p-3">
                  🌟 {aiSummary}
                </p>
              )}
            </div>
          )}

          {/* 댓글 섹션 */}
          <CommentSection roomId={roomId} />
        </div>
      )}

      {/* 참가자 탭 */}
      {activeTab === "participants" && (
        <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6">
          <h2 className="text-lg font-semibold text-gray-900 mb-4 border-b pb-3">
            오늘의 참가자 현황
          </h2>

          <ParticipantListSection participants={participants} />
        </div>
      )}
    </div>
  );
}