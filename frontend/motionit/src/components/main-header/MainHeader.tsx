"use client";

import { useRouter } from "next/navigation";
import { useState, useEffect, useRef } from "react";
import { APP_NAME, CHALLENGE_ROOM, SETTINGS } from "../../constants";

export default function MainHeader() {
  const router = useRouter();
  const [menuOpen, setMenuOpen] = useState(false);
  const menuRef = useRef<HTMLDivElement>(null);

  // ✅ 메뉴 외부 클릭 시 닫기
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
      await fetch(`${process.env.NEXT_PUBLIC_API_BASE_URL}/api/v1/auth/local/logout`, {
        method: "POST",
        credentials: "include", // refreshToken 쿠키 포함
      });
    } catch (error) {
      console.error("🚨 로그아웃 실패:", error);
    } finally {
      setMenuOpen(false);
      router.push("/auth/login");
    }
  };

  return (
    <header className="sticky top-0 z-20 bg-white border-b">
      <div className="mx-auto max-w-6xl px-4 h-16 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <span className="text-2xl font-extrabold tracking-tight">{APP_NAME}</span>
          <span className="ml-2 text-sm text-neutral-500">{CHALLENGE_ROOM}</span>
        </div>

        {/* ⋯ 버튼 + 드롭다운 */}
        <div className="relative" ref={menuRef}>
          <button
            className="h-8 w-8 rounded-full flex items-center justify-center leading-none text-2xl hover:bg-gray-100"
            aria-label="More"
            onClick={() => setMenuOpen((prev) => !prev)}
          >
            {SETTINGS}
          </button>

          {menuOpen && (
            <div className="absolute right-0 mt-2 w-40 bg-white border border-gray-200 rounded-lg shadow-lg">
              <button
                onClick={handleSettings}
                className="block w-full text-left px-4 py-2 text-sm hover:bg-gray-100"
              >
                마이페이지
              </button>
              <button
                onClick={handleLogout}
                className="block w-full text-left px-4 py-2 text-sm text-red-500 hover:bg-gray-100"
              >
                로그아웃
              </button>
            </div>
          )}
        </div>
      </div>
    </header>
  );
}
