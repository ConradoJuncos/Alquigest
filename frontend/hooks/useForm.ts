"use client";

import { useState, useCallback } from "react";

interface UseFormResult<T extends Record<string, unknown>> {
  data: T;
  handleChange: (field: keyof T, value: T[keyof T]) => void;
  setData: React.Dispatch<React.SetStateAction<T>>;
  reset: () => void;
}

export function useForm<T extends Record<string, unknown>>(
  initialData: T
): UseFormResult<T> {
  const [data, setData] = useState<T>(initialData);

  const handleChange = useCallback(
    (field: keyof T, value: T[keyof T]) => {
      setData((prev) => ({ ...prev, [field]: value }));
    },
    []
  );

  const reset = useCallback(() => setData(initialData), [initialData]);

  return { data, handleChange, setData, reset };
}
