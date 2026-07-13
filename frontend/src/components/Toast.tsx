import React, { useEffect, useState } from 'react';

type ToastType = 'success' | 'error' | 'info';
interface ToastItem { id: number; message: string; type: ToastType; }

let listeners: ((t: ToastItem) => void)[] = [];
let counter = 0;

/** Show a toast from anywhere: toast('Added to cart', 'success'). */
export function toast(message: string, type: ToastType = 'info') {
  const item: ToastItem = { id: ++counter, message, type };
  listeners.forEach((l) => l(item));
}

const colors: Record<ToastType, string> = {
  success: 'bg-green-600',
  error: 'bg-red-600',
  info: 'bg-gray-800',
};

export const Toaster: React.FC = () => {
  const [toasts, setToasts] = useState<ToastItem[]>([]);

  useEffect(() => {
    const listener = (t: ToastItem) => {
      setToasts((prev) => [...prev, t]);
      setTimeout(() => setToasts((prev) => prev.filter((x) => x.id !== t.id)), 3500);
    };
    listeners.push(listener);
    return () => { listeners = listeners.filter((l) => l !== listener); };
  }, []);

  return (
    <div className="fixed bottom-4 right-4 z-[100] flex flex-col gap-2">
      {toasts.map((t) => (
        <div
          key={t.id}
          className={`px-4 py-3 rounded-lg shadow-lg text-sm font-medium text-white animate-[fadeIn_0.2s_ease-out] ${colors[t.type]}`}
        >
          {t.message}
        </div>
      ))}
    </div>
  );
};
