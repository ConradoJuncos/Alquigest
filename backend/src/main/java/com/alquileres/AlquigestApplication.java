package com.alquileres;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AlquigestApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlquigestApplication.class, args);
	}

}
