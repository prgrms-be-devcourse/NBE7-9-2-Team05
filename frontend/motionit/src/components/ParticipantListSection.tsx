"use client";

import Image from "next/image";
import { ChallengeMissionStatus } from "@/type";
import { CheckCircle, Circle, Crown } from "lucide-react";

interface ParticipantListSectionProps {
  participants: ChallengeMissionStatus[];
}

const CLOUDFRONT_DOMAIN = process.env.NEXT_PUBLIC_CLOUD_FRONT_DOMAIN || "";

export default function ParticipantListSection({ participants }: ParticipantListSectionProps) {
    if (!participants || participants.length === 0) {
      return <p className="text-gray-500 text-sm">참가자 데이터가 없습니다.</p>;
    }
  
    return (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {participants.map((p) => {
          const profileUrl = p.userProfile?.startsWith("http")
            ? p.userProfile
            : `${CLOUDFRONT_DOMAIN}/${p.userProfile}`;
  
          const isHost = p.isHost === "HOST";
  
          return (
            <div
              key={p.participantId}
              className="flex items-center space-x-6 bg-white border border-gray-100 rounded-3xl shadow-sm hover:shadow-lg transition-all p-6"
            >
                {/* 프로필 이미지 */}
                <div className="relative flex-shrink-0 w-40 h-40 rounded-full overflow-hidden border border-gray-200 bg-gray-100 shadow-md">
                {profileUrl ? (
                    <Image
                    src={profileUrl}
                    alt={`${p.nickname} 프로필`}
                    fill
                    sizes="160px"
                    className="object-cover transition-transform duration-200 hover:scale-110"
                    />
                ) : (
                    <div className="flex items-center justify-center h-full text-sm text-gray-400">
                    No Img
                    </div>
                )}
                </div>
  
              {/* 텍스트 영역 */}
              <div className="flex-1 min-w-0">
                <div className="flex items-center space-x-3">
                <p className="font-bold text-gray-900 truncate text-xl">{p.nickname}</p>
                {isHost && (
                    <span className="inline-flex items-center" title="방장">
                    <Crown
                        size={22}
                        className="text-yellow-500 -mt-[2px]"
                        aria-hidden="true"
                    />
                    <span className="sr-only">방장</span>
                    </span>
                )}
                </div>
                <p
                className={`mt-3 font-medium ${
                    p.completed ? "text-green-600" : "text-gray-600"
                } text-lg`}
                >
                {p.completed ? "오늘의 운동 완료! 💪" : "아직 미완료 🕓"}
                </p>
            </div>

            {/* 완료 상태 아이콘 */}
            <div className="flex-shrink-0" title={p.completed ? "완료" : "미완료"}>
                {p.completed ? (
                <CheckCircle size={30} className="text-green-600" aria-hidden="true" />
                ) : (
                <Circle size={30} className="text-gray-400" aria-hidden="true" />
                )}
                <span className="sr-only">{p.completed ? "완료" : "미완료"}</span>
            </div>
            </div>
          );
        })}
      </div>
    );
  }