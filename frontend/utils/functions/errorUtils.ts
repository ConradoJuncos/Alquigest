export function getErrorMessage(
  error: unknown,
  defaultMsg = "No se pudo conectar con el servidor"
): string {
  if (error instanceof Error) return error.message || defaultMsg;
  if (typeof error === "string") return error || defaultMsg;
  return defaultMsg;
}
