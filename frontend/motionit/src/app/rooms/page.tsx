'use client';

import { useEffect, useState } from 'react';
import { CREATE_NEW_ROOM, ROOM_TAB } from '../../constants';
import { ChallengeStatus } from '../../type';
import MainHeader from '../../components/main-header/MainHeader';
import RoomCard from '../../components/room-card/RoomCard';
import { useRoom } from '../../stores';
import { useChallengeRoomEvents } from '../hooks/useChallengeRoomEvent';
import CreateRoomModal from '../../components/create-room-modal/CreateRoomModal';
import { challengeService } from '../../services';

const TABS = [ChallengeStatus.ALL, ChallengeStatus.JOINABLE, ChallengeStatus.JOINING] as const;
type Tab = (typeof TABS)[number];

export default function RoomList() {
  const [tab, setTab] = useState<Tab>(ChallengeStatus.ALL);
  const {
    rooms,
    loading,
    total,
    page,
    size,
    status,
    error,
    getRooms,
    setPage,
    setSize,
    setStatus,
  } = useRoom();
  const [ open, setOpen ] = useState(false);

  const totalPages = Math.max(1, Math.ceil(total / size));

  useEffect(() => {
    getRooms(page, size, status);
  }, [page, size, status, getRooms]);

  useChallengeRoomEvents();

  if ( loading || !rooms) {
    return <p>Loading...</p>;
  }

  if (error) {
    return <p className='text-red-500'>{error}</p>
  }

  return (
    <main className="min-h-screen bg-neutral-50">

        <MainHeader/>
        {/* Page title */}
        <section className="mx-auto max-w-6xl px-4 pt-6">
            <div className="mt-4 flex justify-between">
                <h1 className="text-xl font-semibold">함께 운동하며 목표를 달성해보세요</h1>
                <button
                    className="rounded-lg px-4 py-2 text-sm font-medium bg-emerald-600 text-white hover:bg-emerald-700 transition"
                    onClick={() => { setOpen(true) }}
                >
                    {`+ ${CREATE_NEW_ROOM}`}
                </button>
            </div>
            
            {/* Tabs */}
            <div className="mt-4 flex gap-2">
            {TABS.map((t) => {
                const active = t === tab;
                return (
                <button
                    key={t}
                    onClick={() => {
                      setStatus(t);
                      setTab(t);
                    }}
                    className={[
                    'px-4 py-2 rounded-full text-sm transition border',
                    active
                        ? 'bg-emerald-600 text-white border-emerald-600'
                        : 'bg-white text-neutral-700 border-gray-400 hover:bg-neutral-50',
                    ].join(' ')}
                >
                    {ROOM_TAB[t]}
                </button>
                );
            })}
            </div>
        </section>

        {/* Cards */}
        <section className="mx-auto max-w-6xl px-4 py-6">
          {rooms.length === 0 ? (
            <p className="text-neutral-500">표시할 방이 없습니다.</p>
            ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {rooms.map((room) => (
                <RoomCard key={room.id} challenge={room} />
              ))}
            </div>
          )}

          {/* Pagination */}
          <div className="mt-8 flex items-center justify-between">
          {/* page size */}
          <div className="flex items-center gap-2 text-sm">
            <span>표시할 방 개수</span>
            <select
              value={size}
              onChange={(e) => setSize(Number(e.target.value))}
              className="border rounded-md px-2 py-1"
            >
              {[10, 20, 30].map((n) => (
                <option key={n} value={n}>{n}</option>
              ))}
            </select>
            <span className="text-neutral-500">총 {total}개</span>
          </div>

          {/* pager */}
          <div className="flex items-center gap-2">
            <button
              className="px-3 py-1 border rounded-md disabled:opacity-50"
              disabled={page <= 0}
              onClick={() => setPage(page - 1)}
            >
              이전
            </button>

            <span className="text-sm">
              {page + 1} / {totalPages}
            </span>

            <button
              className="px-3 py-1 border rounded-md disabled:opacity-50"
              disabled={page + 1 >= totalPages}
              onClick={() => setPage(page + 1)}
            >
              다음
            </button>
          </div>
        </div>
        <CreateRoomModal
          open={open}
          onClose={() => setOpen(false)}
          onCreate={async (data) => {
            try {
              // 1) 필수 값 체크 (imageFile/videoUrl)
              if (!data.imageFile) {
                alert('이미지를 선택하세요.');
                return;
              }
              if (!data.videoUrl) {
                alert('유튜브 URL을 입력하세요.');
                return;
              }

              const file = data.imageFile;
              const fileName = file.name; // 서버는 이 파일명으로 objectKey를 만듦
              const contentType = file.type || 'application/octet-stream';

              // 2) 방 생성 API (JSON)
              const createRes = await challengeService.createRoom({
                title: data.title,
                description: data.description,
                capacity: data.capacity,
                duration: data.durationDays,
                videoUrl: data.videoUrl,
                imageFileName: fileName,
                contentType,
              });

              // axios 인터셉터가 response.data를 반환 → envelope 구조 가정
              // { resultCode, msg, data: CreateRoomResponse }
              const uploadUrl: string | undefined = createRes?.data?.uploadUrl;

              if (!uploadUrl) {
                alert('업로드 URL을 받지 못했습니다.');
                return;
              }

              // 3) presigned URL 로 S3에 직접 업로드 (PUT)
              const putResp = await fetch(uploadUrl, {
                method: 'PUT',
                headers: {
                  'Content-Type': contentType, // 중요!
                },
                body: file,
              });

              if (!putResp.ok) {
                const text = await putResp.text().catch(() => '');
                console.error('S3 업로드 실패:', putResp.status, text);
                alert('이미지 업로드에 실패했습니다.');
                return;
              }

              // 4) UI 갱신
              setOpen(false);
              // 현재 페이지 조건으로 다시 가져오기
              const { getRooms: refetchRooms, page, size, status } = useRoom.getState();
              await refetchRooms(page, size, status);
            } catch (err) {
              console.error(err);
              alert('방 생성 중 오류가 발생했습니다.');
            }
          }}
        />
      </section>
    </main>
  );
}