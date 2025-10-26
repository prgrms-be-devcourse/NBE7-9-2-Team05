/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useParams, useRouter } from "next/navigation";
import { useCallback, useEffect, useRef, useMemo, useState } from "react";
import { challengeService } from "@/services";
import type { ChallengeVideo, ChallengeMissionStatus, ChallengeRoomDetail } from "@/type";
import { UploadVideoForm, CommentSection, VideoListSection } from "@/components";
import ParticipantListSection from "@/components/ParticipantListSection";
import MainHeader from "@/components/main-header/MainHeader";
import { Loader2, Dumbbell, UsersRound, MessageSquareHeart } from "lucide-react";
import { useUser } from "../../../stores";
import { ParticipantRole } from "../../../constants";
import ConfirmModal from "../../../components/confirm-modal/ConfirmModal";

export default function RoomDetailPage() {
  const params = useParams();
  const router = useRouter();
  const roomId = Number(params.roomId);

  const { user } = useUser();

  const [room, setRoom] = useState<ChallengeRoomDetail | null>(null);
  const [activeTab, setActiveTab] = useState<"feed" | "participants">("feed");
  const [videos, setVideos] = useState<ChallengeVideo[]>([]);
  const [loading, setLoading] = useState(true);
  const [missionStatus, setMissionStatus] = useState<string | null>(null);
  const [aiMessage, setAiMessage] = useState<string | null>(null);
  const [isCompleting, setIsCompleting] = useState(false);
  const [participants, setParticipants] = useState<ChallengeMissionStatus[]>([]);
  const [isAuthorized, setIsAuthorized] = useState<boolean | null>(null);
  const [open, setOpen] = useState<boolean>(false);
  const isHost = useMemo(() => {
    const participants = room?.participants;

    const participant = participants?.find((p) => p.nickname === user.nickname);

    return participant?.role === ParticipantRole.HOST;
  }, [room, user]);
  
  const identifiersRef = useRef<{ userId: number | null; participantId: number | null }>({
    userId: null,
    participantId: null,
  });
  const fallbackMessageRef = useRef<string | null>(null);
  const selectAiMessage = useCallback(
    (statuses: ChallengeMissionStatus[]) => {
      const { userId, participantId } = identifiersRef.current;

      const target =
        (participantId != null &&
          statuses.find((status) => status.participantId === participantId)) ||
        (userId != null && statuses.find((status) => status.userId === userId)) ||
        statuses.find((status) => {
          const message = status.aiMessage ?? status.aiSummary;
          return typeof message === "string" && message.trim().length > 0;
        });

      const message = target?.aiMessage ?? target?.aiSummary ?? null;

      if (typeof message !== "string") {
        return null;
      }

      const trimmed = message.trim();
      return trimmed.length > 0 ? trimmed : null;
    },
    [],
  );

  /** 참여자 검증 */
  const checkParticipationStatus = async () => {
    try {
      const res = await challengeService.getParticipationStatus(roomId);
      const payload = (res as any)?.data ?? res;
      const joined = payload?.joined ?? false;

      if (!joined) {
        alert("챌린지 운동방에 참여하지 않았어요. 목록으로 이동할게요.");
        router.push("/rooms");
      } else {
        identifiersRef.current = {
          userId:
            typeof payload?.userId === "number"
              ? payload.userId
              : null,
          participantId:
            typeof payload?.participantId === "number"
              ? payload.participantId
              : null,
        };

        const participationMessage =
          typeof payload?.aiMessage === "string"
            ? payload.aiMessage.trim()
            : typeof payload?.aiSummary === "string"
            ? payload.aiSummary.trim()
            : "";

        const trimmedMessage = participationMessage.length > 0 ? participationMessage : null;

        fallbackMessageRef.current = null;
        setAiMessage(trimmedMessage);
        setIsAuthorized(true);
      }
    } catch (err) {
      console.error("참여 여부 확인 실패:", err);
      alert("운동방 참여 여부 확인 중 오류가 발생했습니다.");
      router.push("/rooms");
    }
  };

  /** 방 상세 조회 */
  const fetchRoomDetail = async () => {
    try {
      const res = await challengeService.getRoomDetail(roomId);
      const roomData = res.data?.data ?? res.data;
      setRoom(roomData);
    } catch (err) {
      console.error("운동방 상세 조회 실패:", err);
    }
  };

  /** 나가기 */
  const handleLeaveRoom = async () => {
    if (!confirm("운동방에서 나가시겠습니까?")) return;

    try {
      await challengeService.leaveChallengeRoom(roomId);
      alert("운동방에서 탈퇴했습니다.");
      router.push("/rooms");
    } catch (err) {
      console.error("운동방 탈퇴 실패:", err);
      alert("운동방 탈퇴 중 오류가 발생했습니다.");
    }
  };

  /** 미션 완료 */
  const handleCompleteMission = async () => {
    if (isCompleting) return;
    setIsCompleting(true);
    setAiMessage(null);
    try {
      await challengeService.completeMission(roomId);
      setMissionStatus("오늘 운동이 완료되었습니다! 💪");

      const statuses = await fetchParticipants();
      const derivedMessage = selectAiMessage(statuses);
      const defaultFallback = "응원 메시지를 불러오지 못했습니다 😢";

      if (derivedMessage) {
        fallbackMessageRef.current = null;
        setAiMessage(derivedMessage);
      } else {
        try {
          const aiRes = await challengeService.getAiSummary(roomId);
          const message = ((aiRes as any)?.data ?? aiRes) as string | null;
          if (typeof message === "string" && message.trim().length > 0) {
            const trimmed = message.trim();
            fallbackMessageRef.current = trimmed;
            setAiMessage(trimmed);
          } else {
            fallbackMessageRef.current = defaultFallback;
            setAiMessage(defaultFallback);
          }
        } catch {
          fallbackMessageRef.current = defaultFallback;
          setAiMessage(defaultFallback);
        }
      }
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

  const handleRemoveRoom = async () => {
    setOpen(true);
  }

  /** 영상 목록 */
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

  /** 참가자 현황 */
  const fetchParticipants = async (): Promise<ChallengeMissionStatus[]> => {
    try {
      const res = await challengeService.getTodayMissions(roomId);
      const statuses: ChallengeMissionStatus[] = Array.isArray((res as any)?.data)
        ? (res as any).data
        : Array.isArray(res)
        ? (res as ChallengeMissionStatus[])
        : [];
      setParticipants(statuses);
      return statuses;
    } catch (err) {
      console.error("참여 현황 불러오기 실패:", err);
      return [];
    }
  };

  useEffect(() => {
    if (!participants.length) {
      if (!fallbackMessageRef.current) {
        setAiMessage(null);
      }
      return;
    }

    const message = selectAiMessage(participants);

    if (message !== null) {
      fallbackMessageRef.current = null;
      setAiMessage(message);
    } else if (!fallbackMessageRef.current) {
      setAiMessage(null);
    }
  }, [participants, selectAiMessage]);

  /** 초기 로드 */
  useEffect(() => {
    if (!roomId) return;
    const init = async () => {
      await checkParticipationStatus();
      await fetchRoomDetail();
      await fetchVideos();
      await fetchParticipants();
    };
    init();
  }, [roomId]);

  /** 렌더링 제어 */
  if (isAuthorized === null || loading)
    return (
      <div className="flex flex-col items-center justify-center min-h-screen text-gray-500">
        <Loader2 className="animate-spin w-6 h-6 mb-2" />
        데이터를 불러오는 중입니다...
      </div>
    );

  if (!isAuthorized)
    return <p className="text-center mt-20 text-red-500">접근 권한이 없습니다.</p>;

  if (!room)
    return (
      <p className="text-center mt-20 text-gray-400">
        방 정보를 불러오는 중입니다...
      </p>
    );

  return (
    <main className="min-h-screen bg-neutral-50">
      <MainHeader />

      {/* 폭 확장 */}
      <div className="max-w-6xl mx-auto p-8 space-y-10">
        {/* 운동방 정보 카드 */}
        <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-6 hover:shadow-md transition-shadow">
          <div className="flex items-center justify-between">
            <h2 className="text-2xl font-bold text-gray-900 flex items-center gap-2">
              <Dumbbell className="text-green-600 w-6 h-6" />
              {room.title}
            </h2>
            <button
              onClick={handleLeaveRoom}
              className="bg-red-600 text-white text-sm px-4 py-2 rounded-lg hover:bg-red-700 transition"
            >
              운동방 나가기
            </button>
          </div>
          {isHost && (
          <div className="flex justify-end mt-2">
              <button
                onClick={handleRemoveRoom}
                className="bg-white border-1 border-red-500 text-red-500 text-sm px-4 py-2 rounded-lg hover:bg-red-700 transition"
              >
                운동방 삭제
              </button>
          </div>
          )}
          <p className="text-gray-600 mt-3 leading-relaxed">{room.description}</p>
          <div className="mt-4 text-sm text-gray-400 flex items-center gap-2">
            <UsersRound className="w-4 h-4" />
            참가자 수: {room.participants?.length ?? 0}명
          </div>
        </div>

        {/* 탭 */}
        <div className="flex border-b border-gray-200">
          {["feed", "participants"].map((tab) => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab as "feed" | "participants")}
              className={`relative px-5 py-3 font-semibold transition-colors ${
                activeTab === tab
                  ? "text-green-600"
                  : "text-gray-500 hover:text-green-600"
              }`}
            >
              {tab === "feed" ? "피드" : "참가자"}
              {activeTab === tab && (
                <span className="absolute bottom-0 left-0 right-0 h-[2px] bg-green-600 rounded-full" />
              )}
            </button>
          ))}
        </div>

        {/* 피드 탭 */}
        {activeTab === "feed" && (
          <div className="bg-white border border-gray-100 rounded-2xl shadow-sm p-8 space-y-10">
            <h2 className="text-xl font-semibold text-gray-900 flex items-center gap-2">
              <MessageSquareHeart className="text-green-600 w-5 h-5" />
              오늘의 운동 영상
            </h2>

            {/* 단일 VideoListSection만 남김 */}
            <VideoListSection
              videos={videos}
              roomId={roomId}
              onRefresh={fetchVideos}
            />

            {/* 업로드 섹션 */}
            <div className="bg-gray-50 rounded-xl p-6 shadow-inner">
              <UploadVideoForm roomId={roomId} onUploadSuccess={fetchVideos} />
            </div>

            {/* 오늘 운동 완료 + 상태 블록 */}
            <div className="bg-gray-50 rounded-xl p-6 shadow-inner space-y-3">
              <div className="flex flex-col sm:flex-row items-center justify-between gap-4">
                <h3 className="text-base font-semibold text-gray-900">오늘 운동 완료</h3>
                <button
                  onClick={handleCompleteMission}
                  disabled={isCompleting}
                  className={`font-medium px-6 py-2 rounded-xl transition ${
                    isCompleting
                      ? "bg-gray-400 text-white cursor-not-allowed"
                      : "bg-green-600 text-white hover:bg-green-700"
                  }`}
                >
                  {isCompleting ? "처리 중..." : "완료"}
                </button>
              </div>

              {/* 상태 메시지 (항상 고정 표시) */}
              <div className="min-h-[30px] mt-2">
                <p
                  className={`text-sm font-medium ${
                    missionStatus
                      ? missionStatus.includes("이미")
                        ? "text-yellow-600"
                        : missionStatus.includes("오류")
                        ? "text-red-600"
                        : "text-green-600"
                      : "text-gray-400"
                  }`}
                >
                  {missionStatus || "오늘도 득근하는 하루되세요 💪"}
                </p>
              </div>
            </div>

            {/* AI 응원문구 */}
            {aiMessage && (
              <div className="bg-green-50 border border-green-100 rounded-xl p-4 shadow-sm animate-fadeIn">
                <p className="text-sm text-gray-700 leading-relaxed">
                  🌟 {aiMessage}
                </p>
              </div>
            )}

            {/* 댓글 섹션 */}
            <div className="bg-white rounded-xl p-6 shadow-sm border border-gray-100">
              <CommentSection roomId={roomId} />
            </div>
          </div>
        )}

        {/* 참가자 탭 */}
        {activeTab === "participants" && (
          <div className="bg-white border border-gray-200 rounded-2xl shadow-sm p-8">
            <h2 className="text-lg font-semibold text-gray-900 mb-4 flex items-center gap-2">
              <UsersRound className="text-green-600 w-5 h-5" />
              오늘의 참가자 현황
            </h2>
            <ParticipantListSection participants={participants} />
          </div>
        )}
      </div>

      <ConfirmModal 
        open={open}
        title={'운동방 삭제'}
        message={`운동방을 정말 삭제하시겠어요?`}
        confirmText={'삭제'}
        cancelText={'취소'}
        onConfirm={async () => {
          await challengeService.deleteRoom(roomId);
          router.push('/rooms');
        }}
        onCancel={() => setOpen(false)}
      />
    </main>
  );
}
