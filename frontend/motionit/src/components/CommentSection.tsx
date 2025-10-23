// 📁 src/components/CommentSection.tsx
"use client";

import { useEffect, useState } from "react";
import { challengeService } from "@/services";
import type { Comment } from "@/type";

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

  /** 수정 취소 */
  const cancelEdit = () => {
    setEditingCommentId(null);
    setEditingContent("");
  };

  /** 댓글 삭제 */
  const handleDeleteComment = async (commentId: number) => {
    if (!confirm("댓글을 삭제하시겠습니까?")) return;
    try {
      await challengeService.deleteComment(roomId, commentId);
      fetchComments(currentPage);
    } catch (err) {
      console.error("댓글 삭제 실패:", err);
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

      {/* 입력창 */}
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

      {/* 댓글 리스트 */}
      {comments.length === 0 ? (
        <p className="text-gray-500 text-sm">아직 댓글이 없습니다.</p>
      ) : (
        <ul className="space-y-3">
          {comments.map((c) => (
            <li key={c.id} className="flex justify-between items-start border border-gray-100 rounded-xl p-3 shadow-sm">
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
                      <button onClick={() => saveEdit(c.id)} className="text-xs bg-blue-500 text-white px-3 py-1 rounded-md hover:bg-blue-600">
                        저장
                      </button>
                      <button onClick={cancelEdit} className="text-xs bg-gray-300 text-gray-700 px-3 py-1 rounded-md hover:bg-gray-400">
                        취소
                      </button>
                    </div>
                  </div>
                ) : (
                  <>
                    <p className={`text-sm ${c.deleted ? "text-gray-400 italic" : "text-gray-700"}`}>{c.content}</p>
                    <p className="text-xs text-gray-400 mt-1">
                      {new Date(c.createdAt).toLocaleString()}
                    </p>
                  </>
                )}
              </div>

              {!c.deleted && editingCommentId !== c.id && (
                <div className="flex flex-col space-y-1 ml-4">
                  <button onClick={() => startEditing(c.id, c.content)} className="text-xs text-blue-500 hover:underline">
                    수정
                  </button>
                  <button onClick={() => handleDeleteComment(c.id)} className="text-xs text-red-500 hover:underline">
                    삭제
                  </button>
                </div>
              )}
            </li>
          ))}
        </ul>
      )}

      {/* 페이지네이션 */}
      {totalPages > 1 && (
        <div className="flex justify-center items-center space-x-2 mt-6">
          <button
            onClick={() => fetchComments(Math.max(currentPage - 1, 0))}
            disabled={currentPage === 0}
            className="px-3 py-1 text-sm border rounded disabled:opacity-50 hover:bg-gray-100"
          >
            이전
          </button>
          {Array.from({ length: totalPages }, (_, i) => (
            <button
              key={i}
              onClick={() => fetchComments(i)}
              className={`px-3 py-1 text-sm border rounded ${
                currentPage === i ? "bg-green-600 text-white" : "hover:bg-gray-100"
              }`}
            >
              {i + 1}
            </button>
          ))}
          <button
            onClick={() => fetchComments(Math.min(currentPage + 1, totalPages - 1))}
            disabled={currentPage === totalPages - 1}
            className="px-3 py-1 text-sm border rounded disabled:opacity-50 hover:bg-gray-100"
          >
            다음
          </button>
        </div>
      )}
    </div>
  );
}