export function formatLocalDate(
  date: Date | string,
  options: Intl.DateTimeFormatOptions = {
    day: "2-digit",
    month: "2-digit",
    year: "numeric",
  }
): string {
  const d = typeof date === "string" ? new Date(date) : date;
  return d.toLocaleDateString("es-AR", options);
}

export function sortByDate<T>(
  items: T[],
  getDate: (item: T) => string | null | undefined,
  direction: "asc" | "desc" = "asc"
): T[] {
  return [...items].sort((a, b) => {
    const da = getDate(a);
    const db = getDate(b);
    if (!da && !db) return 0;
    if (!da) return direction === "asc" ? 1 : -1;
    if (!db) return direction === "asc" ? -1 : 1;
    const diff = new Date(da).getTime() - new Date(db).getTime();
    return direction === "asc" ? diff : -diff;
  });
}

export function convertirFechas(fechaISO: string){
  const [year, month, day] = fechaISO.split("-");
  return `${day}/${month}/${year}`;
};

export function fechaActualValida(): string {
  const fechaActual = new Date();
  const year = fechaActual.getFullYear();
  const month = String(fechaActual.getMonth() + 1).padStart(2, "0"); // +1 porque es 0-based
  const day = String(fechaActual.getDate()).padStart(2, "0");

  return `${day}/${month}/${year}`;
}

export function calcularProximoAumento(fechaInicio: string, periodoMeses: number): string{
  const [year, month, day] = fechaInicio.split("-").map(Number);

  // Crear objeto Date
  const fecha = new Date(year, month - 1, day); // month es 0-based en JS

  // Sumar meses
  fecha.setMonth(fecha.getMonth() + periodoMeses);

  // Formatear como yyyy-mm-dd
  const yyyy = fecha.getFullYear();
  const mm = String(fecha.getMonth() + 1).padStart(2, "0"); // +1 porque es 0-based
  const dd = String(fecha.getDate()).padStart(2, "0");

  return `${dd}/${mm}/${yyyy}`;
}