'use client';

import { useEffect, useRef } from "react";

interface ConfirmModalProps {
    open: boolean;
    title?: string;
    message?: string;
    confirmText?: string;
    cancelText?: string;
    onConfirm: () => void | Promise<void>;
    onCancel: () => void;
}

export default function ConfirmModal({
    open,
    title = '확인',
    message = '챌린지에 참여하시겠어요?',
    confirmText  = '확인',
    cancelText = '취소',
    onConfirm,
    onCancel,
}: ConfirmModalProps) {
    const backdropRef = useRef<HTMLDivElement | null>(null);
    const firstButtonRef = useRef<HTMLButtonElement | null>(null);

    useEffect(() => {
        if (!open) {
            return;
        }

        const t = setTimeout(() => firstButtonRef.current?.focus(), 0);
        const onKey = (e: KeyboardEvent) => {
            if (e.key === 'Escape') {
                onCancel();
            }
        };

        document.addEventListener('keydown', onKey);
        return () => {
            clearTimeout(t);
            document.removeEventListener('keydown', onKey);
        };
    }, [open, onCancel]);

    if (!open) {
        return null;
    }

    return (
        <div
            ref={backdropRef}
            className="fixed inset-0 z-50"
            role="dialog"
            aria-modal="true"
            onMouseDown={(e) => {
            if (e.target === backdropRef.current) onCancel();
            }}
        >
        {/* Backdrop */}
        <div className="absolute inset-0 bg-black/40" />
  
        {/* Panel */}
        <div className="absolute inset-0 flex items-center justify-center p-4">
          <div className="w-full max-w-md rounded-2xl bg-white shadow-xl">
            <div className="px-6 py-5">
              <h2 className="text-lg font-semibold">{title}</h2>
              <p className="mt-2 text-sm text-neutral-600 whitespace-pre-line">
                {message}
              </p>
  
              <div className="mt-6 flex justify-end gap-2">
                <button
                  ref={firstButtonRef}
                  type="button"
                  onClick={onCancel}
                  className="rounded-lg border px-4 py-2 text-sm hover:bg-neutral-50"
                >
                  {cancelText}
                </button>
                <button
                  type="button"
                  onClick={onConfirm}
                  className="rounded-lg bg-emerald-600 px-4 py-2 text-sm font-medium text-white hover:bg-emerald-700"
                >
                  {confirmText}
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
}
