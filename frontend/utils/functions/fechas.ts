export function convertirFechas(fechaISO: string){
  const [year, month, day] = fechaISO.split("-");
  return `${day}/${month}/${year}`;
};

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