package com.alquileres.config;

import com.alquileres.model.Rol;
import com.alquileres.model.RolNombre;
import com.alquileres.repository.RolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RolRepository rolRepository;

    @Override
    public void run(String... args) throws Exception {
        // Inicializar roles si no existen
        if (rolRepository.count() == 0) {
            rolRepository.save(new Rol(RolNombre.ROLE_ADMINISTRADOR));
            rolRepository.save(new Rol(RolNombre.ROLE_ABOGADA));
            rolRepository.save(new Rol(RolNombre.ROLE_SECRETARIA));

            System.out.println("Roles inicializados en la base de datos");
        }
    }
}
