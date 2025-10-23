/* eslint-disable @typescript-eslint/no-explicit-any */
// 📁 src/components/CommentSection.tsx
"use client";

import { useEffect, useState } from "react";
import { challengeService } from "@/services";
import type { Comment } from "@/type";
import { Heart } from "lucide-react"; // 💖 아이콘 (lucide-react 패키지 사용)

interface CommentSectionProps {
  roomId: number;
}

export default function CommentSection({ roomId }: CommentSectionProps) {
  const [comments, setComments] = useState<Comment[]>([]);
  const [newComment, setNewComment] = useState("");
  const [loading, setLoading] = useState(true);
  const [editingCommentId, setEditingCommentId] = useState<number | null>(null);
  const [editingContent, setEditingContent] = useState("");
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  /** 댓글 목록 불러오기 */
  const fetchComments = async (page = 0) => {
    try {
      const res = await challengeService.getComments(roomId, page, 10);
      setComments(res.data?.content || []);
      setTotalPages(res.data?.totalPages || 0);
      setCurrentPage(res.data?.number || 0);
    } catch (err) {
      console.error("댓글 불러오기 실패:", err);
    } finally {
      setLoading(false);
    }
  };

  /** 댓글 등록 */
  const handleAddComment = async () => {
    if (!newComment.trim()) return alert("댓글 내용을 입력하세요.");
    try {
      await challengeService.createComment(roomId, newComment.trim());
      setNewComment("");
      fetchComments(currentPage);
    } catch (err) {
      console.error("댓글 작성 실패:", err);
      // alert는 client.ts에서 이미 처리됨
    }
  };

  /** Enter 키로 댓글 등록 */
  const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
    if ((e.nativeEvent as any).isComposing || e.key !== "Enter" || e.shiftKey) return;
    e.preventDefault();
    handleAddComment();
  };

  /** 수정 시작 */
  const startEditing = (id: number, content: string) => {
    setEditingCommentId(id);
    setEditingContent(content);
  };

  /** 수정 저장 */
  const saveEdit = async (commentId: number) => {
    if (!editingContent.trim()) return alert("수정할 내용을 입력하세요.");
    try {
      await challengeService.editComment(roomId, commentId, editingContent.trim());
      setEditingCommentId(null);
      setEditingContent("");
      fetchComments(currentPage);
    } catch (err) {
      console.error("댓글 수정 실패:", err);
    }
  };

  const cancelEdit = () => {
    setEditingCommentId(null);
    setEditingContent("");
  };

  /** 댓글 삭제 */
  const handleDelete = async (id: number) => {
    if (!confirm("댓글을 삭제하시겠습니까?")) return;
    try {
      await challengeService.deleteComment(roomId, id);
      fetchComments(currentPage);
    } catch (err) {
      console.error("댓글 삭제 실패:", err);
    }
  };

  /** 좋아요 토글 */
  const handleToggleLike = async (commentId: number) => {
    // 1️⃣ 현재 상태 즉시 반영 (Optimistic Update)
    setComments((prev) =>
      prev.map((c) =>
        c.id === commentId
          ? {
              ...c,
              isLiked: !c.isLiked,
              likeCount: c.isLiked ? c.likeCount - 1 : c.likeCount + 1,
            }
          : c
      )
    );

    try {
      // 2️⃣ 서버 반영
      const res = await challengeService.toggleCommentLike(commentId);
      const updated = res.data;

      // 3️⃣ 서버 응답 기준으로 최종 동기화 (정확한 값으로)
      setComments((prev) =>
        prev.map((c) =>
          c.id === commentId
            ? { ...c, likeCount: updated.likeCount, isLiked: updated.isLiked }
            : c
        )
      );
    } catch (err: any) {
      console.error("좋아요 토글 실패:", err);

      // 4️⃣ Optimistic Lock 충돌 시 롤백
      if (err?.response?.data?.msg?.includes("LIKE_TOGGLE_FAILED")) {
        alert("잠시 후 다시 시도해주세요. 다른 사용자의 동작과 충돌했습니다.");

        // 롤백 (UI 되돌리기)
        setComments((prev) =>
          prev.map((c) =>
            c.id === commentId
              ? {
                  ...c,
                  isLiked: !c.isLiked, // 원래대로 복원
                  likeCount: c.isLiked ? c.likeCount + 1 : c.likeCount - 1,
                }
              : c
          )
        );
      }
    }
  };

  useEffect(() => {
    if (roomId) fetchComments(0);
  }, [roomId]);

  if (loading) {
    return <p className="text-gray-500 text-sm mt-6">댓글 불러오는 중...</p>;
  }

  return (
    <div className="mt-8 border-t pt-6">
      <h3 className="text-base font-semibold text-gray-900 mb-3">댓글</h3>

      {/* 입력 */}
      <div className="flex space-x-2 mb-4">
        <input
          type="text"
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="댓글을 입력하세요... (Enter로 등록)"
          className="flex-1 border border-gray-300 rounded-lg px-3 py-2 text-sm focus:outline-none focus:ring focus:ring-green-200"
        />
        <button
          onClick={handleAddComment}
          className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm hover:bg-green-700 transition"
        >
          등록
        </button>
      </div>

      {/* 리스트 */}
      {comments.length === 0 ? (
        <p className="text-gray-500 text-sm">아직 댓글이 없습니다.</p>
      ) : (
        <ul className="space-y-3">
          {comments.map((c) => (
            <li
              key={c.id}
              className="flex justify-between items-start border border-gray-100 rounded-xl p-3 shadow-sm"
            >
              <div className="flex-1">
                <p className="text-sm font-semibold text-gray-800">{c.authorNickname}</p>

                {editingCommentId === c.id ? (
                  <div className="mt-1">
                    <input
                      type="text"
                      value={editingContent}
                      onChange={(e) => setEditingContent(e.target.value)}
                      className="w-full border border-gray-300 rounded-md px-2 py-1 text-sm focus:outline-none focus:ring focus:ring-blue-200"
                    />
                    <div className="flex space-x-2 mt-2">
                      <button
                        onClick={() => saveEdit(c.id)}
                        className="text-xs bg-blue-500 text-white px-3 py-1 rounded-md hover:bg-blue-600"
                      >
                        저장
                      </button>
                      <button
                        onClick={cancelEdit}
                        className="text-xs bg-gray-300 text-gray-700 px-3 py-1 rounded-md hover:bg-gray-400"
                      >
                        취소
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <p
                      className={`text-sm ${
                        c.deleted ? "text-gray-400 italic" : "text-gray-700"
                      }`}
                    >
                      {c.content}
                    </p>
                    <p className="text-xs text-gray-400 mt-1">
                      {new Date(c.createdAt).toLocaleString()}
                    </p>
                  </>
                )}
              </div>

              {/* 오른쪽 버튼 그룹 */}
              <div className="flex flex-col items-end space-y-1 ml-4">
                {/* ❤️ 좋아요 버튼 */}
                {!c.deleted && (
                  <button
                    onClick={() => handleToggleLike(c.id)}
                    className="flex items-center space-x-1 text-xs text-gray-500 hover:text-red-500 transition"
                  >
                    <Heart
                      size={14}
                      fill={c.isLiked ? "red" : "none"}
                      stroke={c.isLiked ? "red" : "gray"}
                    />
                    <span>{c.likeCount}</span>
                  </button>
                )}

                {/* ✏️ 수정 / 삭제 */}
                {!c.deleted && editingCommentId !== c.id && (
                  <>
                    <button
                      onClick={() => startEditing(c.id, c.content)}
                      className="text-xs text-blue-500 hover:underline"
                    >
                      수정
                    </button>
                    <button
                      onClick={() => handleDelete(c.id)}
                      className="text-xs text-red-500 hover:underline"
                    >
                      삭제
                    </button>
                  </>
                )}
              </div>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}