"use client";

import { ChevronLeft, ChevronRight, ChevronsLeft, ChevronsRight } from "lucide-react";

interface PaginationProps {
  currentPage: number;        // 현재 페이지 (0부터 시작)
  totalPages: number;         // 전체 페이지 수
  onPageChange: (page: number) => void;
  groupSize?: number;         // 페이지 버튼 묶음 크기 (기본값 10)
  className?: string;         // 커스텀 스타일 (선택)
}

export default function Pagination({
  currentPage,
  totalPages,
  onPageChange,
  groupSize = 10,
  className = "",
}: PaginationProps) {
  if (totalPages <= 1) return null;

  const currentGroup = Math.floor(currentPage / groupSize);
  const startPage = currentGroup * groupSize;
  const endPage = Math.min(startPage + groupSize, totalPages);
  const visiblePages = Array.from({ length: endPage - startPage }, (_, i) => startPage + i);

  return (
    <div
      className={`flex justify-center items-center mt-6 space-x-1 select-none flex-wrap ${className}`}
    >
      {/* 처음으로 */}
      <button
        disabled={currentPage === 0}
        onClick={() => onPageChange(0)}
        className={`flex items-center space-x-1 px-2 py-1 rounded-md text-sm transition-all duration-150
          ${
            currentPage === 0
              ? "text-gray-300 bg-gray-100 cursor-not-allowed"
              : "text-gray-600 bg-white border hover:bg-green-50 hover:text-green-700"
          }`}
      >
        <ChevronsLeft size={14} />
        <span>처음</span>
      </button>

      {/* 이전 그룹 */}
      <button
        disabled={currentGroup === 0}
        onClick={() => onPageChange(Math.max(startPage - 1, 0))}
        className={`flex items-center space-x-1 px-3 py-1.5 rounded-lg text-sm font-medium transition-all duration-150
          ${
            currentGroup === 0
              ? "text-gray-300 bg-gray-100 cursor-not-allowed"
              : "text-gray-600 bg-white border hover:bg-green-50 hover:text-green-700"
          }`}
      >
        <ChevronLeft size={16} />
        <span>이전</span>
      </button>

      {/* 숫자 버튼 */}
      <div className="flex space-x-1 mx-1">
        {visiblePages.map((idx) => (
          <button
            key={idx}
            onClick={() => onPageChange(idx)}
            className={`w-8 h-8 flex items-center justify-center rounded-md text-sm font-medium transition-all
              ${
                idx === currentPage
                  ? "bg-green-600 text-white shadow-sm"
                  : "text-gray-600 hover:bg-gray-100"
              }`}
          >
            {idx + 1}
          </button>
        ))}
      </div>

      {/* 다음 그룹 */}
      <button
        disabled={endPage >= totalPages}
        onClick={() => onPageChange(endPage)}
        className={`flex items-center space-x-1 px-3 py-1.5 rounded-lg text-sm font-medium transition-all duration-150
          ${
            endPage >= totalPages
              ? "text-gray-300 bg-gray-100 cursor-not-allowed"
              : "text-gray-600 bg-white border hover:bg-green-50 hover:text-green-700"
          }`}
      >
        <span>다음</span>
        <ChevronRight size={16} />
      </button>

      {/* 마지막으로 */}
      <button
        disabled={currentPage >= totalPages - 1}
        onClick={() => onPageChange(totalPages - 1)}
        className={`flex items-center space-x-1 px-2 py-1 rounded-md text-sm transition-all duration-150
          ${
            currentPage >= totalPages - 1
              ? "text-gray-300 bg-gray-100 cursor-not-allowed"
              : "text-gray-600 bg-white border hover:bg-green-50 hover:text-green-700"
          }`}
      >
        <span>끝</span>
        <ChevronsRight size={14} />
      </button>
    </div>
  );
}