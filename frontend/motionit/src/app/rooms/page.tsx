// app/page.tsx
'use client';

import { useState } from 'react';
import MainHeader from '../components/main-header/MainHeader';
import { CREATE_NEW_ROOM } from '../../constants';
import { Challenge, ChallengeStatus, RoomCta } from '../../type';
import RoomCard from '../components/room-card/RoomCard';

// TODO: Fetch GET Challenge room api
const DATA: Challenge[] = [
  {
    id: 1,
    title: '30일 홈트 챌린지',
    description: '집에서 할 수 있는 다양한 운동으로 30일간 꾸준히 운동해보세요!',
    imageUrl: '/images/homeworkout.jpg',
    current: 12,
    capacity: 20,
    dday: 15,
    status: ChallengeStatus.ALL,
    cta: RoomCta.GET_IN,
  },
  {
    id: 2,
    title: '아침 요가 챌린지',
    description: '매일 아침 요가로 하루를 상쾌하게 시작해보세요',
    imageUrl: '/images/yoga.jpg',
    current: 8,
    capacity: 15,
    dday: 22,
    status: ChallengeStatus.JOINABLE,
    cta: RoomCta.GET_IN,
  },
  {
    id: 3,
    title: '런닝 챌린지',
    description: '매일 조금씩 달리기 거리를 늘려가며 체력을 기르는 챌린지',
    imageUrl: '/images/running.jpg',
    current: 25,
    capacity: 30,
    dday: 8,
    status: ChallengeStatus.JOINING,
    cta: RoomCta.JOIN,
  },
  {
    id: 4,
    title: '근력 운동 챌린지',
    description: '주 3회 근력 루틴으로 전신 밸런스를 잡아보세요',
    imageUrl: '/images/strength.jpg',
    current: 10,
    capacity: 25,
    dday: 30,
    status: ChallengeStatus.ALL,
    cta: RoomCta.JOIN,
  },
];

const TABS = [ChallengeStatus.ALL, ChallengeStatus.JOINABLE, ChallengeStatus.JOINING] as const;
type Tab = (typeof TABS)[number];

export default function Page() {
  const [tab, setTab] = useState<Tab>(ChallengeStatus.ALL);

  const filtered =
    tab === '전체' ? DATA : DATA.filter((c) => c.status === tab);

  return (
    <main className="min-h-screen bg-neutral-50">

        <MainHeader/>
        {/* Page title */}
        <section className="mx-auto max-w-6xl px-4 pt-6">
            <div className="mt-4 flex justify-between">
                <h1 className="text-xl font-semibold">함께 운동하며 목표를 달성해보세요</h1>
                <button
                    className="rounded-lg px-4 py-2 text-sm font-medium bg-emerald-600 text-white hover:bg-emerald-700 transition"
                    onClick={() => alert('TODO Create New Room')}
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
                    onClick={() => setTab(t)}
                    className={[
                    'px-4 py-2 rounded-full text-sm transition border',
                    active
                        ? 'bg-emerald-600 text-white border-emerald-600'
                        : 'bg-white text-neutral-700 border-gray-400 hover:bg-neutral-50',
                    ].join(' ')}
                >
                    {t}
                </button>
                );
            })}
            </div>
        </section>

        {/* Cards */}
        <section className="mx-auto max-w-6xl px-4 py-6">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {filtered.map((c) => (
                    <RoomCard key={c.id} challenge={c}/>
                ))}
            </div>
        </section>
    </main>
  );
}