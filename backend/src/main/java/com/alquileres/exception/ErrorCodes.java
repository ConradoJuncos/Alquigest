package com.alquileres.exception;

public class ErrorCodes {

    // Propietario error codes
    public static final String DNI_DUPLICADO = "DNI_DUPLICADO";
    public static final String EMAIL_DUPLICADO = "EMAIL_DUPLICADO";
    public static final String PROPIETARIO_NO_ENCONTRADO = "PROPIETARIO_NO_ENCONTRADO";

    // Inmueble error codes
    public static final String INMUEBLE_NO_ENCONTRADO = "INMUEBLE_NO_ENCONTRADO";
    public static final String INMUEBLE_YA_ALQUILADO = "INMUEBLE_YA_ALQUILADO";

    // Inquilino error codes
    public static final String CUIL_DUPLICADO = "CUIL_DUPLICADO";
    public static final String INQUILINO_NO_ENCONTRADO = "INQUILINO_NO_ENCONTRADO";

    // General error codes
    public static final String VALIDACION_ERROR = "VALIDACION_ERROR";
    public static final String RECURSO_NO_ENCONTRADO = "RECURSO_NO_ENCONTRADO";
    public static final String ERROR_INTERNO = "ERROR_INTERNO";
}
