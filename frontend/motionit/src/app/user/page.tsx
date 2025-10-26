"use client";

import Image from "next/image";
import Link from "next/link";
import {
  ChangeEvent,
  useCallback,
  useEffect,
  useMemo,
  useRef,
  useState,
} from "react";
import { useRouter } from "next/navigation";

import { APP_NAME, SETTINGS } from "@/constants";
import { storageService, userService, type UserProfile } from "@/services";

type StatusMessage =
  | { type: "success"; message: string }
  | { type: "error"; message: string }
  | null;

const PROFILE_IMAGE_SIZE = 112;
const DEFAULT_PROFILE_IMAGE_URL =
  "https://ui-avatars.com/api/?name=User&background=4F46E5&color=fff";
const NICKNAME_MIN_LENGTH = 3;
const NICKNAME_MAX_LENGTH = 10;

export default function UserProfilePage() {
  const router = useRouter();
  const fileInputRef = useRef<HTMLInputElement | null>(null);
  const menuRef = useRef<HTMLDivElement | null>(null);

  const [menuOpen, setMenuOpen] = useState(false);
  const [profile, setProfile] = useState<UserProfile | null>(null);
  const [nicknameInput, setNicknameInput] = useState("");
  const [status, setStatus] = useState<StatusMessage>(null);
  const [loading, setLoading] = useState(true);
  const [nicknameUpdating, setNicknameUpdating] = useState(false);
  const [photoUploading, setPhotoUploading] = useState(false);

  const loadProfile = useCallback(async () => {
    setLoading(true);
    try {
      const data = await userService.getProfile();
      setProfile(data);
      setNicknameInput(data.nickname ?? "");
      setStatus(null);
    } catch (error) {
      const message =
        error instanceof Error
          ? error.message
          : "프로필 정보를 불러오지 못했어요. 잠시 후 다시 시도해 주세요.";
      setStatus({ type: "error", message });
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    void loadProfile();
  }, [loadProfile]);

  useEffect(() => {
    function handleClickOutside(event: MouseEvent) {
      if (menuRef.current && !menuRef.current.contains(event.target as Node)) {
        setMenuOpen(false);
      }
    }

    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  const handleSettings = () => {
    setMenuOpen(false);
    router.push("/user");
  };

  const handleLogout = async () => {
    try {
      await fetch(
        `${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/auth/local/logout`,
        {
          method: "POST",
          credentials: "include",
        },
      );
    } catch (error) {
      console.error("로그아웃 실패:", error);
    } finally {
      setMenuOpen(false);
      router.push("/auth/login");
    }
  };

  const handleOpenFilePicker = () => {
    fileInputRef.current?.click();
  };

  const handleNicknameChange = (event: ChangeEvent<HTMLInputElement>) => {
    setNicknameInput(event.target.value);
  };

  const handleNicknameUpdate = useCallback(async () => {
    if (!profile) {
      return;
    }

    const trimmed = nicknameInput.trim();

    if (!trimmed) {
      setStatus({
        type: "error",
        message: "닉네임을 입력해 주세요.",
      });
      return;
    }

    if (
      trimmed.length < NICKNAME_MIN_LENGTH ||
      trimmed.length > NICKNAME_MAX_LENGTH
    ) {
      setStatus({
        type: "error",
        message: `닉네임은 ${NICKNAME_MIN_LENGTH}자 이상 ${NICKNAME_MAX_LENGTH}자 이하로 입력해 주세요.`,
      });
      return;
    }

    if (trimmed === (profile.nickname ?? "")) {
      setStatus({
        type: "error",
        message: "현재 닉네임과 동일합니다.",
      });
      return;
    }

    setNicknameUpdating(true);
    setStatus(null);
    try {
      const updated = await userService.updateProfile({ nickname: trimmed });
      setProfile(updated);
      setNicknameInput(updated.nickname ?? "");
      setStatus({ type: "success", message: "닉네임을 수정했습니다." });
    } catch (error) {
      const message =
        error instanceof Error
          ? error.message
          : "닉네임을 수정하지 못했어요. 잠시 후 다시 시도해 주세요.";
      setStatus({ type: "error", message });
    } finally {
      setNicknameUpdating(false);
    }
  }, [nicknameInput, profile]);

  const handlePhotoChange = useCallback(
    async (event: ChangeEvent<HTMLInputElement>) => {
      const file = event.target.files?.[0];
      event.target.value = "";

      if (!file) {
        return;
      }

      if (!file.type.startsWith("image/")) {
        setStatus({
          type: "error",
          message: "이미지 파일만 업로드할 수 있어요.",
        });
        return;
      }

      if (!profile) {
        return;
      }

      setPhotoUploading(true);
      setStatus(null);

      try {
        const { objectKey, uploadUrl } = await storageService.createUploadUrl({
          originalFileName: file.name,
          contentType: file.type,
        });

        await fetch(uploadUrl, {
          method: "PUT",
          headers: {
            "Content-Type": file.type,
          },
          body: file,
        });

        const updated = await userService.updateProfile({
          userProfile: objectKey,
        });
        setProfile(updated);
        setStatus({ type: "success", message: "프로필 사진을 변경했습니다." });
      } catch (error) {
        const message =
          error instanceof Error
            ? error.message
            : "프로필 사진을 변경하지 못했어요. 잠시 후 다시 시도해 주세요.";
        setStatus({ type: "error", message });
      } finally {
        setPhotoUploading(false);
      }
    },
    [profile],
  );

  const handlePasswordChangeClick = () => {
    router.push("/auth/password");
  };

  const sanitizedProfileImageSrc = useMemo(() => {
    const candidate =
      profile?.userProfileUrl?.trim() ?? profile?.userProfile?.trim();

    if (!candidate) {
      return null;
    }

    if (!/^https?:\/\//i.test(candidate)) {
      return candidate;
    }

    try {
      const parsed = new URL(candidate);
      if (
        parsed.protocol === "http:" &&
        parsed.hostname !== "localhost" &&
        parsed.hostname !== "127.0.0.1"
      ) {
        parsed.protocol = "https:";
        return parsed.toString();
      }
      return parsed.toString();
    } catch {
      return candidate.replace(/^http:\/\//i, "https://");
    }
  }, [profile?.userProfileUrl, profile?.userProfile]);

  const profileImageSrc =
    profile && (sanitizedProfileImageSrc ?? DEFAULT_PROFILE_IMAGE_URL);

  return (
    <div className="min-h-screen bg-[#f6f9fb]">
      <header className="border-b border-[#e3ebf3] bg-white">
        <div className="mx-auto flex max-w-5xl items-center justify-between px-6 py-5">
          <Link href="/rooms" className="text-2xl font-black text-[#0f253c]">
            {APP_NAME}
          </Link>
          <div className="relative" ref={menuRef}>
            <button
              className="flex h-8 w-8 items-center justify-center rounded-full text-2xl leading-none hover:bg-gray-100"
              aria-label="More"
              onClick={() => setMenuOpen((prev) => !prev)}
            >
              {SETTINGS}
            </button>
            {menuOpen && (
              <div className="absolute right-0 mt-2 w-40 rounded-lg border border-gray-200 bg-white shadow-lg">
                <button
                  onClick={handleSettings}
                  className="block w-full px-4 py-2 text-left text-sm hover:bg-gray-100"
                >
                  마이페이지
                </button>
                <button
                  onClick={handleLogout}
                  className="block w-full px-4 py-2 text-left text-sm text-red-500 hover:bg-gray-100"
                >
                  로그아웃
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      <main className="mx-auto max-w-5xl px-4 py-10 sm:px-6">
        <div className="max-w-3xl">
          <h1 className="text-2xl font-semibold text-[#152941]">내 정보</h1>
          <p className="mt-2 text-sm text-[#7b8b9c]">
            프로필 정보를 확인하고 수정할 수 있습니다.
          </p>
        </div>

        {status && (
          <div
            className={`mt-6 rounded-2xl border px-4 py-3 text-sm ${
              status.type === "success"
                ? "border-emerald-200 bg-emerald-50 text-emerald-700"
                : "border-red-200 bg-red-50 text-red-600"
            }`}
          >
            {status.message}
          </div>
        )}

        <section className="mt-8 rounded-3xl border border-[#e3ebf3] bg-white px-8 py-10 shadow-[0_12px_40px_rgba(15,35,60,0.08)]">
          {loading ? (
            <div className="space-y-6">
              <div className="h-28 w-28 rounded-full bg-[#eef3f9]" />
              <div className="h-10 w-full rounded-2xl bg-[#eef3f9]" />
              <div className="h-10 w-48 rounded-2xl bg-[#eef3f9]" />
            </div>
          ) : profile ? (
            <>
              <h2 className="text-lg font-semibold text-[#152941]">
                프로필 정보
              </h2>

              <div className="mt-6 flex flex-wrap items-center gap-6">
                <div
                  className="flex h-[112px] w-[112px] items-center justify-center overflow-hidden rounded-full border border-[#d8e2ec] bg-[#f2f6fa]"
                  style={{ width: PROFILE_IMAGE_SIZE, height: PROFILE_IMAGE_SIZE }}
                >
                  {profileImageSrc ? (
                    <Image
                      src={profileImageSrc}
                      alt="프로필 사진"
                      width={PROFILE_IMAGE_SIZE}
                      height={PROFILE_IMAGE_SIZE}
                      className="h-full w-full object-cover"
                    />
                  ) : (
                    <span className="text-2xl font-semibold text-[#7b8b9c]">
                      {profile.nickname?.slice(0, 1) ?? "?"}
                    </span>
                  )}
                </div>

                <div>
                  <p className="text-sm font-semibold text-[#1e3147]">
                    프로필 사진
                  </p>
                  <p className="mt-1 text-sm text-[#7b8b9c]">
                    새로운 사진으로 변경해 보세요.
                  </p>
                  <button
                    type="button"
                    onClick={handleOpenFilePicker}
                    disabled={photoUploading}
                    className="mt-3 rounded-full border border-[#0aa37a] px-4 py-2 text-sm font-semibold text-[#0aa37a] transition hover:bg-[#0aa37a] hover:text-white disabled:cursor-not-allowed disabled:opacity-60"
                  >
                    {photoUploading ? "업로드 중..." : "사진 변경"}
                  </button>
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept="image/*"
                    className="hidden"
                    onChange={handlePhotoChange}
                  />
                </div>
              </div>

              <div className="mt-10 space-y-5">
                <div>
                  <label className="mb-2 block text-sm font-semibold text-[#1e3147]">
                    닉네임
                  </label>
                  <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
                    <input
                      type="text"
                      value={nicknameInput}
                      onChange={handleNicknameChange}
                      placeholder="닉네임을 입력하세요"
                      maxLength={NICKNAME_MAX_LENGTH}
                      className="w-full rounded-2xl border border-[#d8e2ec] bg-white px-4 py-3 text-sm text-[#14263d] placeholder:text-[#aab6c2] focus:border-transparent focus:outline-none focus:ring-2 focus:ring-[#0aa37a]"
                    />
                    <button
                      type="button"
                      onClick={handleNicknameUpdate}
                      disabled={nicknameUpdating}
                      className="inline-flex h-11 min-w-[80px] items-center justify-center whitespace-nowrap rounded-2xl bg-[#0aa37a] px-6 text-sm font-semibold text-white transition hover:bg-[#068c68] disabled:cursor-not-allowed disabled:opacity-70"
                    >
                      {nicknameUpdating ? "수정 중..." : "수정"}
                    </button>
                  </div>
                </div>

                <div>
                  <span className="mb-2 block text-sm font-semibold text-[#1e3147]">
                    이메일
                  </span>
                  <div className="rounded-2xl border border-[#eef3f9] bg-[#f7fafc] px-4 py-3 text-sm text-[#66788a]">
                    {profile.email}
                  </div>
                </div>
              </div>
            </>
          ) : (
            <div className="rounded-2xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-600">
              프로필 정보를 불러오지 못했어요. 잠시 후 다시 시도해 주세요.
            </div>
          )}
        </section>

        <section className="mt-6 rounded-3xl border border-[#e3ebf3] bg-white px-8 py-6 shadow-[0_8px_30px_rgba(15,35,60,0.05)]">
          <div className="flex flex-col items-start justify-between gap-4 sm:flex-row sm:items-center">
            <div>
              <h2 className="text-base font-semibold text-[#152941]">
                비밀번호 변경
              </h2>
              <p className="mt-1 text-sm text-[#7b8b9c]">
                새로운 비밀번호로 계정을 안전하게 유지하세요.
              </p>
            </div>
            <button
              type="button"
              onClick={handlePasswordChangeClick}
              className="rounded-full border border-[#0aa37a] px-5 py-2 text-sm font-semibold text-[#0aa37a] transition hover:bg-[#0aa37a] hover:text-white"
            >
              변경하기
            </button>
          </div>
        </section>
      </main>
    </div>
  );
}
