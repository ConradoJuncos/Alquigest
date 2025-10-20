package com.alquileres.service;

import com.alquileres.dto.PropietarioDTO;
import com.alquileres.dto.InmuebleDTO;
import com.alquileres.dto.InquilinoDTO;
import com.alquileres.dto.ContratoCreateDTO;
import com.alquileres.exception.BusinessException;
import com.alquileres.exception.ErrorCodes;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class PropietarioServiceIntegrationTest {

    @Autowired
    private PropietarioService propietarioService;

    @Autowired
    private InmuebleService inmuebleService;

    @Autowired
    private InquilinoService inquilinoService;

    @Autowired
    private ContratoService contratoService;

    @Test
    void desactivarPropietario_shouldThrowException_whenHasActiveContracts() {
        // Crear propietario
        PropietarioDTO propietario = createTestPropietario();
        PropietarioDTO propietarioCreado = propietarioService.crearPropietario(propietario);

        // Crear inmueble
        InmuebleDTO inmueble = createTestInmueble(propietarioCreado.getId());
        InmuebleDTO inmuebleCreado = inmuebleService.crearInmueble(inmueble);

        // Crear inquilino
        InquilinoDTO inquilino = createTestInquilino();
        InquilinoDTO inquilinoCreado = inquilinoService.crearInquilino(inquilino);

        // Crear contrato vigente
        ContratoCreateDTO contrato = createTestContrato(inmuebleCreado.getId(), inquilinoCreado.getId());
        contratoService.crearContrato(contrato);

        // Intentar desactivar propietario - debería fallar
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            propietarioService.desactivarPropietario(propietarioCreado.getId());
        });

        assertEquals(ErrorCodes.PROPIETARIO_TIENE_CONTRATOS_VIGENTES, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("contratos vigentes"));
    }

    @Test
    void desactivarPropietario_shouldSucceed_whenNoActiveContracts() {
        // Crear propietario
        PropietarioDTO propietario = createTestPropietario();
        PropietarioDTO propietarioCreado = propietarioService.crearPropietario(propietario);

        // Crear inmueble sin contratos
        InmuebleDTO inmueble = createTestInmueble(propietarioCreado.getId());
        inmuebleService.crearInmueble(inmueble);

        // Desactivar propietario - debería funcionar
        assertDoesNotThrow(() -> {
            propietarioService.desactivarPropietario(propietarioCreado.getId());
        });

        // Verificar que está desactivado
        PropietarioDTO propietarioDesactivado = propietarioService.obtenerPropietarioPorId(propietarioCreado.getId());
        assertFalse(propietarioDesactivado.getEsActivo());
    }

    private PropietarioDTO createTestPropietario() {
        PropietarioDTO propietario = new PropietarioDTO();
        propietario.setNombre("Test");
        propietario.setApellido("Propietario");
        propietario.setCuil("12345678" + System.currentTimeMillis() % 1000); // CUIL único
        propietario.setTelefono("123456789");
        propietario.setEmail("test" + System.currentTimeMillis() % 1000 + "@test.com"); // Email único
        propietario.setDireccion("Test Address");
        propietario.setEsActivo(true);
        return propietario;
    }

    private InmuebleDTO createTestInmueble(Long propietarioId) {
        InmuebleDTO inmueble = new InmuebleDTO();
        inmueble.setPropietarioId(propietarioId);
        inmueble.setDireccion("Test Inmueble Address");
        inmueble.setTipoInmuebleId(1L); // Asumiendo que existe tipo 1
        inmueble.setEstado(1); // Asumiendo que existe estado 1 (Disponible)
        inmueble.setSuperficie(new java.math.BigDecimal("100.0"));
        inmueble.setEsAlquilado(false);
        inmueble.setEsActivo(true);
        return inmueble;
    }

    private InquilinoDTO createTestInquilino() {
        InquilinoDTO inquilino = new InquilinoDTO();
        inquilino.setNombre("Test");
        inquilino.setApellido("Inquilino");
        inquilino.setCuil("20-12345678-9");
        inquilino.setTelefono("987654321");
        inquilino.setEsActivo(true);
        return inquilino;
    }

    private ContratoCreateDTO createTestContrato(Long inmuebleId, Long inquilinoId) {
        ContratoCreateDTO contrato = new ContratoCreateDTO();
        contrato.setInmuebleId(inmuebleId);
        contrato.setInquilinoId(inquilinoId);
        contrato.setFechaInicio("01/10/2025");
        contrato.setFechaFin("01/10/2026");
        contrato.setMonto(new java.math.BigDecimal("50000.0"));
        contrato.setPorcentajeAumento(new java.math.BigDecimal("10.0"));
        contrato.setAumentaConIcl(false);
        contrato.setPeriodoAumento(12);
        return contrato;
    }
}
