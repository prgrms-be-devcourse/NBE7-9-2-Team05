import { useEffect, useRef, useState } from "react";

type CreateRoomPayload = {
    title: string;
    description: string;
    capacity: number;
    durationDays: number;
    imageFile: File | null;
    videoUrl: string | null;
}

interface CreateRoomModalProps {
    open: boolean;
    onClose: () => void;
    onCreate: (data: CreateRoomPayload) => void | Promise<void>;
}

export default function CreateRoomModal({ open, onClose, onCreate }: CreateRoomModalProps) {
    const [title, setTitle] = useState('');
    const [desc, setDesc] = useState('');
    const [capacity, setCapacity] = useState<number>(10);
    const [duration, setDuration] = useState<number>(30);
    const [imageFile, setImageFile] = useState<File | null>(null);
    const [imagePreview, setImagePreview] = useState<string | null>(null);
    const [videoUrl, setVideoUrl] = useState('');
    const [errors, setErrors] = useState<Record<string, string>>({});

    const dialogRef = useRef<HTMLDivElement | null>(null);
    const firstFieldRef = useRef<HTMLInputElement | null>(null);

    // 유튜브 URL 간단 검증
    const isValidYouTube = (url: string) => {
        try {
            const u = new URL(url);
            if (!['www.youtube.com', 'youtube.com', 'youtu.be', 'm.youtube.com'].includes(u.hostname)) return false;
            // youtu.be/<id> 또는 youtube.com/watch?v=<id>
            return u.hostname === 'youtu.be' ? !!u.pathname.slice(1) : !!(u.searchParams.get('v') || u.pathname.startsWith('/shorts/'));
        } catch {
            return false;
        }
    };

    // 이미지 미리보기
    useEffect(() => {
        if (!imageFile) {
            setImagePreview(null);
            return;
        }
        const url = URL.createObjectURL(imageFile);
        setImagePreview(url);
        return () => URL.revokeObjectURL(url);
    }, [imageFile]);

    // 모달 오픈 시 포커스
    useEffect(() => {
        if (open) {
            const t = setTimeout(() => firstFieldRef.current?.focus(), 0);
            return () => clearTimeout(t);
        }
    }, [open]);

    // ESC 닫기
    useEffect(() => {
        if (!open) return;
            const onKey = (e: KeyboardEvent) => {
            if (e.key === 'Escape') onClose();
        };
        document.addEventListener('keydown', onKey);
        return () => document.removeEventListener('keydown', onKey);
    }, [open, onClose]);

    const validate = () => {
        const e: Record<string, string> = {};
        if (!title.trim()) e.title = '운동방 제목을 입력하세요.';
        if (!desc.trim()) e.desc = '설명을 입력하세요.';
        if (!capacity || capacity < 1) e.capacity = '최대 인원은 1 이상이어야 합니다.';
        if (!duration || duration < 1) e.duration = '기간(일)은 1 이상이어야 합니다.';
        if (!videoUrl.trim()) e.videoUrl = '유튜브 URL을 입력하세요.';
        if (!imageFile) e.image = '이미지를 선택하세요.';
        setErrors(e);
        return Object.keys(e).length === 0;
    };

    const submit = async (ev: React.FormEvent) => {
        ev.preventDefault();
        if (!validate()) return;
        await onCreate({
            title: title.trim(),
            description: desc.trim(),
            capacity,
            durationDays: duration,
            imageFile,
            videoUrl: videoUrl.trim() || null,
        });
    };

    if (!open) return null;

    return (
        <div
            ref={dialogRef}
            className="fixed inset-0 z-50"
            role="dialog"
            aria-modal="true"
            onMouseDown={(e) => {
                // 바깥 클릭 닫기
                if (e.target === dialogRef.current) onClose();
            }}
        >
            {/* Backdrop */}
            <div className="absolute inset-0 bg-black/40" />

            {/* Panel */}
            <div className="absolute inset-0 flex items-start justify-center overflow-auto">
                <div className="mt-8 mb-12 w-full max-w-lg rounded-2xl bg-white shadow-xl">
                {/* Header */}
                <div className="flex items-center justify-between px-6 py-4 border-b">
                    <h2 className="text-xl font-bold">운동방 만들기</h2>
                    <button
                        className="h-8 w-8 inline-flex items-center justify-center rounded-full hover:bg-neutral-100"
                        onClick={onClose}
                        aria-label="닫기"
                    >
                    ✕
                    </button>
                </div>

                <form onSubmit={submit} className="px-6 py-4 space-y-4">
                    {/* 제목 */}
                    <div>
                    <label className="block text-sm font-medium mb-1">운동방 제목</label>
                    <input
                        ref={firstFieldRef}
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="운동방 제목을 입력하세요"
                        className="w-full rounded-lg border px-3 py-2 outline-none focus:ring-2 focus:ring-emerald-500"
                    />
                    {errors.title && <p className="mt-1 text-sm text-red-600">{errors.title}</p>}
                    </div>

                    {/* 설명 */}
                    <div>
                    <label className="block text-sm font-medium mb-1">설명</label>
                    <textarea
                        value={desc}
                        onChange={(e) => setDesc(e.target.value)}
                        placeholder="운동방에 대한 설명을 입력하세요"
                        rows={4}
                        className="w-full rounded-lg border px-3 py-2 outline-none focus:ring-2 focus:ring-emerald-500"
                    />
                    {errors.desc && <p className="mt-1 text-sm text-red-600">{errors.desc}</p>}
                    </div>

                    {/* 최대 인원 / 기간(일) */}
                    <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium mb-1">최대 인원</label>
                        <input
                            type="number"
                            min={1}
                            value={capacity}
                            onChange={(e) => setCapacity(Number(e.target.value))}
                            className="w-full rounded-lg border px-3 py-2 outline-none focus:ring-2 focus:ring-emerald-500"
                        />
                        {errors.capacity && <p className="mt-1 text-sm text-red-600">{errors.capacity}</p>}
                    </div>
                    <div>
                        <label className="block text-sm font-medium mb-1">기간 (일)</label>
                        <input
                            type="number"
                            min={1}
                            value={duration}
                            onChange={(e) => setDuration(Number(e.target.value))}
                            className="w-full rounded-lg border px-3 py-2 outline-none focus:ring-2 focus:ring-emerald-500"
                        />
                        {errors.duration && <p className="mt-1 text-sm text-red-600">{errors.duration}</p>}
                    </div>
                    </div>

                    {/* 이미지 업로드 */}
                    <div>
                    <label className="block text-sm font-medium mb-1">운동방 이미지</label>
                    <div className="flex items-center gap-3">
                        <input
                            type="file"
                            accept="image/*"
                            onChange={(e) => setImageFile(e.target.files?.[0] ?? null)}
                            className="w-full rounded-lg border px-3 py-2 file:mr-4 file:rounded-md file:border file:px-3 file:py-1 file:bg-neutral-50 file:hover:bg-neutral-100"
                        />
                        {errors.image && <p className="mt-1 text-sm text-red-600">{errors.image}</p>}
                    </div>
                    {imagePreview && (
                        <div className="mt-3">
                        <img
                            src={imagePreview}
                            alt="preview"
                            className="h-36 w-full object-cover rounded-lg border"
                        />
                        </div>
                    )}
                    </div>

                    <div>
                        <label className="block text-sm font-medium mb-1">운동 영상 (YouTube URL)</label>
                        <input
                            value={videoUrl}
                            onChange={(e) => setVideoUrl(e.target.value)}
                            placeholder="https://www.youtube.com/watch?v=..."
                            className="w-full rounded-lg border px-3 py-2 outline-none focus:ring-2 focus:ring-emerald-500"
                        />
                        {errors.videoUrl && <p className="mt-1 text-sm text-red-600">{errors.videoUrl}</p>}
                    </div>

                    {/* Footer */}
                    <div className="flex items-center justify-end gap-2 pt-2">
                        <button
                            type="button"
                            onClick={onClose}
                            className="rounded-lg border px-4 py-2 text-sm hover:bg-neutral-50"
                        >
                            취소
                        </button>
                        <button
                            type="submit"
                            className="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white hover:bg-emerald-700"
                        >
                        생성하기
                        </button>
                    </div>
                    </form>
                </div>
            </div>
        </div>
    );
}