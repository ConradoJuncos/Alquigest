package com.alquileres.exception;

public class ErrorCodes {

    // Propietario error codes
    public static final String DNI_DUPLICADO = "DNI_DUPLICADO";
    public static final String EMAIL_DUPLICADO = "EMAIL_DUPLICADO";
    public static final String PROPIETARIO_NO_ENCONTRADO = "PROPIETARIO_NO_ENCONTRADO";

    // Inmueble error codes
    public static final String INMUEBLE_NO_ENCONTRADO = "INMUEBLE_NO_ENCONTRADO";
    public static final String INMUEBLE_YA_ALQUILADO = "INMUEBLE_YA_ALQUILADO";
    public static final String INMUEBLE_NO_DISPONIBLE = "INMUEBLE_NO_DISPONIBLE";
    public static final String INMUEBLE_TIENE_CONTRATOS_VIGENTES = "INMUEBLE_TIENE_CONTRATOS_VIGENTES";

    // TipoInmueble error codes
    public static final String TIPO_INMUEBLE_DUPLICADO = "TIPO_INMUEBLE_DUPLICADO";
    public static final String TIPO_INMUEBLE_NO_ENCONTRADO = "TIPO_INMUEBLE_NO_ENCONTRADO";

    // Inquilino error codes
    public static final String CUIL_DUPLICADO = "CUIL_DUPLICADO";
    public static final String INQUILINO_NO_ENCONTRADO = "INQUILINO_NO_ENCONTRADO";
    public static final String INQUILINO_TIENE_CONTRATOS_VIGENTES = "INQUILINO_TIENE_CONTRATOS_VIGENTES";

    // EstadoContrato error codes
    public static final String ESTADO_CONTRATO_NO_ENCONTRADO = "ESTADO_CONTRATO_NO_ENCONTRADO";
    public static final String ESTADO_CONTRATO_YA_EXISTE = "ESTADO_CONTRATO_YA_EXISTE";

    // Contrato error codes
    public static final String CONTRATO_NO_ENCONTRADO = "CONTRATO_NO_ENCONTRADO";
    public static final String RANGO_DE_FECHAS_INVALIDO = "RANGO_DE_FECHAS_INVALIDO";
    public static final String FORMATO_FECHA_INVALIDO = "FORMATO_FECHA_INVALIDO";
    public static final String ERROR_CALCULO_FECHA = "ERROR_CALCULO_FECHA";

    // General error codes
    public static final String VALIDACION_ERROR = "VALIDACION_ERROR";
    public static final String RECURSO_NO_ENCONTRADO = "RECURSO_NO_ENCONTRADO";
    public static final String ERROR_INTERNO = "ERROR_INTERNO";
}
