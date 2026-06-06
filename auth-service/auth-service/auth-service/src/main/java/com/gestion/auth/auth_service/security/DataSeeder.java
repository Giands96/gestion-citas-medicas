package com.gestion.auth.auth_service.security;

import com.gestion.auth.auth_service.credential.service.CredencialService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder extends CommandLineRunner {

    private final CredencialService credencialService;

    String correoAdmin = "admin@email.com";

    String passwordAdmin = "admin123";

    @Override
    public void run(String... args) throws Exception  {

    }

}
