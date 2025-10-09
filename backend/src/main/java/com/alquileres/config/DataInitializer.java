package com.alquileres.config;

import com.alquileres.model.Rol;
import com.alquileres.model.RolNombre;
import com.alquileres.model.TipoInmueble;
import com.alquileres.model.EstadoContrato;
import com.alquileres.model.EstadoInmueble;
import com.alquileres.model.MotivoCancelacion;
import com.alquileres.model.TipoServicio;
import com.alquileres.repository.RolRepository;
import com.alquileres.repository.TipoInmuebleRepository;
import com.alquileres.repository.EstadoContratoRepository;
import com.alquileres.repository.EstadoInmuebleRepository;
import com.alquileres.repository.MotivoCancelacionRepository;
import com.alquileres.repository.TipoServicioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TipoInmuebleRepository tipoInmuebleRepository;

    @Autowired
    private EstadoContratoRepository estadoContratoRepository;

    @Autowired
    private EstadoInmuebleRepository estadoInmuebleRepository;

    @Autowired
    private MotivoCancelacionRepository motivoCancelacionRepository;

    @Autowired
    private TipoServicioRepository tipoServicioRepository;

    @Override
    public void run(String... args) throws Exception {
        // Inicializar roles si no existen
        if (rolRepository.count() == 0) {
            rolRepository.save(new Rol(RolNombre.ROLE_ADMINISTRADOR));
            rolRepository.save(new Rol(RolNombre.ROLE_ABOGADA));
            rolRepository.save(new Rol(RolNombre.ROLE_SECRETARIA));

            System.out.println("Roles inicializados en la base de datos");
        }

        // Inicializar tipos de inmueble si no existen
        if (tipoInmuebleRepository.count() == 0) {
            tipoInmuebleRepository.save(new TipoInmueble("Departamento"));
            tipoInmuebleRepository.save(new TipoInmueble("Casa"));
            tipoInmuebleRepository.save(new TipoInmueble("Local Comercial"));
            tipoInmuebleRepository.save(new TipoInmueble("Oficina"));
            tipoInmuebleRepository.save(new TipoInmueble("Depósito"));
            tipoInmuebleRepository.save(new TipoInmueble("Terreno"));
            tipoInmuebleRepository.save(new TipoInmueble("Otro"));

            System.out.println("Tipos de inmueble inicializados en la base de datos");
        }

        // Inicializar estados de contrato si no existen
        if (estadoContratoRepository.count() == 0) {
            estadoContratoRepository.save(new EstadoContrato("Vigente"));
            estadoContratoRepository.save(new EstadoContrato("No Vigente"));
            estadoContratoRepository.save(new EstadoContrato("Cancelado"));

            System.out.println("Estados de contrato inicializados en la base de datos");
        }

        // Inicializar estados de inmueble si no existen
        if (estadoInmuebleRepository.count() == 0) {
            estadoInmuebleRepository.save(new EstadoInmueble("Disponible"));
            estadoInmuebleRepository.save(new EstadoInmueble("En Reparacion"));
            estadoInmuebleRepository.save(new EstadoInmueble("Inactivo"));
            estadoInmuebleRepository.save(new EstadoInmueble("Alquilado"));

            System.out.println("Estados de inmueble inicializados en la base de datos");
        }

        // Inicializar motivos de cancelación si no existen
        if (motivoCancelacionRepository.count() == 0) {
            motivoCancelacionRepository.save(new MotivoCancelacion("Locador Rescinde", "Se violaron cláusulas específicas del contrato"));
            motivoCancelacionRepository.save(new MotivoCancelacion("Locatario Rescinde", "Otros motivos"));

            System.out.println("Motivos de cancelación inicializados en la base de datos");
        }

        // Inicializar tipos de servicio si no existen
        if (tipoServicioRepository.count() == 0) {
            tipoServicioRepository.save(new TipoServicio("Luz"));
            tipoServicioRepository.save(new TipoServicio("Agua"));
            tipoServicioRepository.save(new TipoServicio("Gas"));
            tipoServicioRepository.save(new TipoServicio("Rentas"));
            tipoServicioRepository.save(new TipoServicio("Municipalidad"));

            System.out.println("Tipos de servicio inicializados en la base de datos");
        }
    }
}
