package com.gestion.user.user_service.bootstrap;

import com.gestion.user.user_service.client.AuthServiceClient;
import com.gestion.user.user_service.client.dto.CredencialRequest;
import com.gestion.user.user_service.dto.request.UsuarioRequest;
import com.gestion.user.user_service.dto.response.UsuarioResponse;
import com.gestion.user.user_service.entity.Usuario;
import com.gestion.user.user_service.enums.Rol;
import com.gestion.user.user_service.repository.UsuarioRepository;
import com.gestion.user.user_service.service.UsuarioService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final AuthServiceClient authServiceClient;

    private static final String ADMIN_NOMBRES = "Admin";
    private static final String ADMIN_APELLIDOS = "Sistema";
    private static final String ADMIN_CORREO = "admin@email.com";
    private static final String ADMIN_PASSWORD = "admin123";
    @Autowired
    private UsuarioService usuarioService;



    @Override
    public void run(String... args) {

        UsuarioRequest admin = new UsuarioRequest();
        admin.setNombres(ADMIN_NOMBRES);
        admin.setApellidos(ADMIN_APELLIDOS);
        admin.setCorreo(ADMIN_CORREO);
        admin.setPassword(ADMIN_PASSWORD);
        admin.setRol(Rol.ADMIN);
        UsuarioResponse guardado = usuarioService.crearUsuario(admin);

        System.out.println("Usuario admin creado: " + guardado);


    }
}
