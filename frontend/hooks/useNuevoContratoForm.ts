import { useState, useCallback } from 'react';
import { Contrato } from '@/types/Contrato';
import { convertirFechas, calcularProximoAumento } from '@/utils/functions/fechas';

export interface DatosAdicionales {
  dniPropietario: string;
  cuilInquilino: string;
  superficieInmueble: string;
  nombrePropietario: string;
  apellidoPropietario: string;
  direccionInmueble: string;
  nombreInquilino: string;
  apellidoInquilino: string;
  tipoInmuebleId: number;
}

const contratoInicial: Contrato = {
  id: 0,
  inmuebleId: 0,
  inquilinoId: 0,
  fechaInicio: '',
  fechaFin: '',
  monto: 0,
  porcentajeAumento: 0,
  estadoContratoId: 1,
  aumentaConIcl: true,
  pdfPath: '',
  tipoAumento: '',
  periodoAumento: 0,
  fechaAumento: '',
};

const datosAdicionalesInicial: DatosAdicionales = {
  dniPropietario: '',
  cuilInquilino: '',
  superficieInmueble: '',
  nombrePropietario: '',
  apellidoPropietario: '',
  direccionInmueble: '',
  nombreInquilino: '',
  apellidoInquilino: '',
  tipoInmuebleId: 0,
};

export function useNuevoContratoForm() {
  const [formData, setFormData] = useState<Contrato>(contratoInicial);
  const [datosAdicionales, setDatosAdicionales] = useState<DatosAdicionales>(datosAdicionalesInicial);
  const [step, setStep] = useState(1);
  const [montoDisplay, setMontoDisplay] = useState('');

  const formatMontoVisual = useCallback((value: number) => {
    if (isNaN(value)) return '';
    return value.toString().replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  }, []);

  const handleInputChange = useCallback((field: string, value: any) => {
    setFormData(prev => ({ ...prev, [field]: value }));
  }, []);

  const handleMontoChange = useCallback((rawValue: string) => {
    const raw = rawValue.replace(/[^0-9]/g, '');
    if (raw === '') {
      setMontoDisplay('');
      setFormData(prev => ({ ...prev, monto: 0 }));
      return;
    }
    const num = parseInt(raw, 10);
    setFormData(prev => ({ ...prev, monto: num }));
    setMontoDisplay(`$ ${formatMontoVisual(num)}`);
  }, [formatMontoVisual]);

  const onBlurMonto = useCallback(() => {
    if (montoDisplay === '' || formData.monto === 0) return;
    setMontoDisplay(`$ ${formatMontoVisual(formData.monto)}`);
  }, [montoDisplay, formData.monto, formatMontoVisual]);

  const selectInmueble = useCallback((inmueble: any, propietario: any) => {
    handleInputChange('inmuebleId', inmueble?.id?.toString() ?? '');
    setDatosAdicionales(prev => ({
      ...prev,
      superficieInmueble: inmueble?.superficie || 'No especificada',
      dniPropietario: propietario?.dni || '',
      nombrePropietario: propietario?.nombre || '',
      apellidoPropietario: propietario?.apellido || '',
      direccionInmueble: inmueble?.direccion || '',
      tipoInmuebleId: inmueble?.tipoInmuebleId || 0,
    }));
  }, [handleInputChange]);

  const selectInquilino = useCallback((inquilino: any) => {
    handleInputChange('inquilinoId', inquilino?.id?.toString() ?? '');
    setDatosAdicionales(prev => ({
      ...prev,
      cuilInquilino: inquilino?.cuil || '',
      nombreInquilino: inquilino?.nombre || '',
      apellidoInquilino: inquilino?.apellido || '',
    }));
  }, [handleInputChange]);

  const isStepValid = useCallback((currentStep: number) => {
    switch (currentStep) {
      case 1:
        return formData.inmuebleId !== 0 && formData.inquilinoId !== 0;
      case 2:
        return formData.fechaInicio !== '' && formData.fechaFin !== '';
      case 3:
        if (formData.tipoAumento === 'ICL') {
          return formData.monto > 0;
        }
        return formData.monto > 0 && formData.tipoAumento !== '' && formData.porcentajeAumento > 0;
      default:
        return true;
    }
  }, [formData]);

  const resetForm = useCallback(() => {
    setFormData(contratoInicial);
    setDatosAdicionales(datosAdicionalesInicial);
    setMontoDisplay('');
    setStep(1);
  }, []);

  const prepararContratoParaEnvio = useCallback(() => {
    const fechaInicioConvert = convertirFechas(formData.fechaInicio);
    const fechaFinConvert = convertirFechas(formData.fechaFin);
    const proximoAumento = calcularProximoAumento(fechaInicioConvert, formData.periodoAumento);
    return {
      ...formData,
      fechaInicio: fechaInicioConvert,
      fechaFin: fechaFinConvert,
      fechaAumento: proximoAumento,
    };
  }, [formData]);

  return {
    formData,
    setFormData,
    datosAdicionales,
    setDatosAdicionales,
    step,
    setStep,
    montoDisplay,
    setMontoDisplay,
    formatMontoVisual,
    handleInputChange,
    handleMontoChange,
    onBlurMonto,
    selectInmueble,
    selectInquilino,
    isStepValid,
    resetForm,
    prepararContratoParaEnvio,
  };
}
