"use client";

import { useState, useCallback } from "react";

interface UseAsyncResult<T> {
  execute: (...args: unknown[]) => Promise<T | undefined>;
  loading: boolean;
  error: string | null;
  data: T | null;
  reset: () => void;
}

export function useAsync<T>(
  fn: (...args: unknown[]) => Promise<T>
): UseAsyncResult<T> {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [data, setData] = useState<T | null>(null);

  const execute = useCallback(
    async (...args: unknown[]) => {
      setLoading(true);
      setError(null);
      try {
        const result = await fn(...args);
        setData(result);
        return result;
      } catch (err: unknown) {
        const msg = err instanceof Error ? err.message : "Error desconocido";
        setError(msg);
        return undefined;
      } finally {
        setLoading(false);
      }
    },
    [fn]
  );

  const reset = useCallback(() => {
    setLoading(false);
    setError(null);
    setData(null);
  }, []);

  return { execute, loading, error, data, reset };
}
