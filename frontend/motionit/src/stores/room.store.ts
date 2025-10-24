/* eslint-disable @typescript-eslint/no-explicit-any */
import { create } from "zustand";
import { ChallengeStatus, RoomSummary } from "../type";
import { challengeService } from "../services";

interface RoomProps {
    rooms: RoomSummary[];
    total: number;
    page: number;
    size: number;
    status: ChallengeStatus;
    loading: boolean;
    error?: string;

    getRooms: (page?: number, size?: number, status?: ChallengeStatus) => Promise<void>;
    setPage: (page: number) => void;
    setSize: (size: number) => void;
    setStatus: (status: ChallengeStatus) => void;
}

export const useRoom = create<RoomProps>((set, get) => ({
    rooms: [],
    total: 0,
    page: 0,
    size: 20,
    status: ChallengeStatus.ALL,
    loading: false,
    error: undefined,

    setPage: (page) => set({ page }),
    setSize: (size) => set({ size }),
    setStatus: (status) => set({ status, page: 0 }),
    getRooms: async (page, size, status) => {
        const state = get();
        const qPage = page ?? state.page;
        const qSize = size ?? state.size;
        const qStatus = status ?? state.status;

        set({ loading: true, error: undefined });

        try {
            const res = await challengeService.getRooms(qPage, qSize);

            const envelope = (res && 'data' in res) ? (res as any) : { data: res };
            const data = envelope.data;

            const roomsRaw = Array.isArray(data?.rooms) ? data.rooms : [];
            const total = Number.isFinite(data?.total) ? data.total : roomsRaw.length;

            const filteredRooms = roomsRaw.filter((room: RoomSummary) => {
                if (qStatus === ChallengeStatus.ALL) {
                    return (
                        room.status === ChallengeStatus.JOINABLE ||
                        room.status === ChallengeStatus.JOINING
                    );
                }
                if (qStatus === ChallengeStatus.JOINABLE) {
                    return room.status === ChallengeStatus.JOINABLE;
                }
                return room.status === ChallengeStatus.JOINING;
            });

            set({
                rooms: filteredRooms,
                total,
                loading: false,
                page: qPage,
                size: qSize,
                status: qStatus,
            });
        } catch (e: any) {
            set({ loading: false, error: e?.message ?? '목록 조회 실패' });
        }
    }
}));