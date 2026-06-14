package com.gestion.user.user_service.client;

import com.gestion.user.user_service.client.dto.CredencialRequest;
import com.gestion.user.user_service.client.dto.CredencialResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "${auth-service.url:http://localhost:8081}", path = "/api/auth")
public interface AuthServiceClient {

    @PostMapping("/credentials")
    CredencialResponse createCredential(@RequestBody CredencialRequest request);
}
