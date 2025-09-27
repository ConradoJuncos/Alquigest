package com.alquileres.config;

import com.alquileres.model.Rol;
import com.alquileres.model.RolNombre;
import com.alquileres.model.TipoInmueble;
import com.alquileres.repository.RolRepository;
import com.alquileres.repository.TipoInmuebleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TipoInmuebleRepository tipoInmuebleRepository;

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
    }
}
