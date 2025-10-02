export interface Contrato {
    id: number
    inmuebleId: number
    inquilinoId: number
    fechaInicio: string
    fechaFin: string
    monto: number
    porcentajeAumento: number
    estadoContratoId: number
    aumentaConIcl: boolean
    pdfPath: string
    tipoAumento: string
    periodoAumento: number
    fechaAumento: string
}